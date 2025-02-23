package endterm.service

import endterm.exception.CustomException
import endterm.model.Document
import endterm.model.enums.DocTypeEnum
import endterm.repository.DocumentRepository
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

@Service
class DocumentService(
    private val tokenService: TokenService,
    private val documentRepository: DocumentRepository
) {

    private val logger = LoggerFactory.getLogger(DocumentService::class.java)

    fun generateBase64(nationality: String): String {
        try {
            val pdfBytes = generateResume(nationality)
            val base64code = Base64.getEncoder().encodeToString(pdfBytes)

            logger.info("Saving document")
            val document = Document()
            document.type = DocTypeEnum.RESUME.value
            document.personId = tokenService.personId
            document.base64code = base64code
            documentRepository.save(document)

            return base64code
        } catch (e: Exception) {
            logger.error("Error generating document", e)
            throw CustomException("Failed to save document or generate a document")
        }
    }

    fun generateResume(nationality: String): ByteArray {
        val data = tokenService.dataToMap(nationality)
        val docxTemplate = FileInputStream(File("src/files/cv_template.docx"))
        val document = XWPFDocument(docxTemplate)
        for (paragraph in document.paragraphs) {
            replacePlaceholdersInParagraph(paragraph, data)
        }

        for (table in document.tables) {
            for (row in table.rows) {
                for (cell in row.tableCells) {
                    for (paragraph in cell.paragraphs) {
                        replacePlaceholdersInParagraph(paragraph, data)
                    }
                }
            }
        }
        val tempDocx = File.createTempFile("resume", ".docx")
        FileOutputStream(tempDocx).use { document.write(it) }
        return convertDocxToPdf(tempDocx)
    }

    private fun replacePlaceholdersInParagraph(paragraph: XWPFParagraph, data: Map<String, String>) {
        val text = paragraph.text
        var modifiedText = text
        data.forEach { (key, value) ->
            modifiedText = modifiedText.replace("{{$key}}", value)
        }
        if (text != modifiedText) {
            while (paragraph.runs.size > 1) {
                paragraph.removeRun(1)
            }
            if (paragraph.runs.isNotEmpty()) {
                val run = paragraph.runs[0]
                run.setText(modifiedText, 0)
            } else {
                paragraph.createRun().setText(modifiedText, 0)
            }
        }
    }

    private fun convertDocxToPdf(docxFile: File): ByteArray {
        val pdfFile = File.createTempFile("resume", ".pdf")
        try {
            val command = listOf("soffice", "--headless", "--convert-to", "pdf", "--outdir",
                pdfFile.parent, docxFile.absolutePath)

            val process = ProcessBuilder(command)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                throw RuntimeException("PDF conversion failed with exit code: $exitCode")
            }
            val actualPdfFile = File(pdfFile.parent, docxFile.nameWithoutExtension + ".pdf")
            if (!actualPdfFile.exists()) {
                throw RuntimeException("PDF file was not created")
            }
            return actualPdfFile.readBytes()
        } catch (e: Exception) {
            throw RuntimeException("Failed to convert DOCX to PDF", e)
        } finally {
            docxFile.delete()
            pdfFile.delete()
        }
    }

}