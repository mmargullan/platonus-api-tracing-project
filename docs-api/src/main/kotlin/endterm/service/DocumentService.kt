package endterm.service

import endterm.exception.CustomException
import endterm.model.Document
import endterm.model.enums.DocTypeEnum
import endterm.repository.DocumentRepository
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class DocumentService(
    @Value("\${cv.template.dir}") private val templateDir: String,
    private val tokenService: TokenService,
    private val documentRepository: DocumentRepository
) {
    private val logger = LoggerFactory.getLogger(DocumentService::class.java)

    fun generateBase64(nationality: String): String {
        try {
            val fileLink = generateResume(nationality)

            logger.info("Saving document metadata to DB, fileLink=$fileLink")
            val document = Document().apply {
                type = DocTypeEnum.RESUME.value
                personId = tokenService.personId
                this.fileLink = fileLink
            }
            documentRepository.save(document)

            return Base64.getEncoder().encodeToString(File(fileLink).readBytes())
        } catch (e: Exception) {
            logger.error("Error generating document", e)
            throw CustomException("Failed to generate or save document")
        }
    }

    private fun generateResume(nationality: String): String {
        val data = tokenService.dataToMap(nationality)
        FileInputStream(File("$templateDir/cv_template.docx")).use { fis ->
            val document = XWPFDocument(fis)

            document.paragraphs.forEach { replacePlaceholdersInParagraph(it, data) }
            document.tables.forEach { table ->
                table.rows.forEach { row ->
                    row.tableCells.forEach { cell ->
                        cell.paragraphs.forEach { p ->
                            replacePlaceholdersInParagraph(p, data)
                        }
                    }
                }
            }

            val tempDocx = File.createTempFile("resume-", ".docx")
            FileOutputStream(tempDocx).use { out -> document.write(out) }

            val tempPdf = File.createTempFile("resume-", ".pdf")
            val exitCode = ProcessBuilder(
                "soffice", "--headless",
                "--convert-to", "pdf",
                "--outdir", tempPdf.parent,
                tempDocx.absolutePath
            )
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()

            if (exitCode != 0) {
                throw RuntimeException("PDF conversion failed, exitCode=$exitCode")
            }

            val generatedPdf = Paths.get(tempPdf.parent, tempDocx.nameWithoutExtension + ".pdf").toFile()
            val finalPdf = Paths.get(templateDir, "${tokenService.firstName}_resume.pdf").toFile()
            generatedPdf.copyTo(finalPdf, overwrite = true)
            tempDocx.delete()
            tempPdf.delete()

            logger.info("PDF saved to: ${finalPdf.absolutePath}")
            return finalPdf.absolutePath
        }
    }

    private fun replacePlaceholdersInParagraph(paragraph: XWPFParagraph, data: Map<String, String>) {
        var text = paragraph.text
        data.forEach { (key, value) ->
            text = text.replace("{{$key}}", value)
        }
        if (paragraph.runs.isNotEmpty()) {
            paragraph.runs[0].setText(text, 0)
            for (i in paragraph.runs.size - 1 downTo 1) {
                paragraph.removeRun(i)
            }
        } else {
            paragraph.createRun().setText(text, 0)
        }
    }

    fun downloadFirstDocumentByPersonId(personId: Long): ResponseEntity<Resource> {
        val doc = documentRepository.findByPersonId(personId)
            .firstOrNull()
            ?: throw CustomException("Document not found for personId=$personId")

        val path = Paths.get(doc.fileLink).toAbsolutePath().normalize()
        if (!Files.exists(path)) {
            throw CustomException("File not found at path=${path}")
        }

        val data = Files.readAllBytes(path)
        val resource = ByteArrayResource(data)
        val fileName = path.fileName.toString()

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(resource)
    }
}
