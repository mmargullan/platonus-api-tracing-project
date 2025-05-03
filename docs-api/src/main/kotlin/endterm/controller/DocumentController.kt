package endterm.controller

import endterm.model.Document
import endterm.repository.DocumentRepository
import endterm.service.DocumentService
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/document")
class DocumentController(
    private val documentRepository: DocumentRepository,
    private val documentService: DocumentService
) {

    @PostMapping("/generate")
    fun generateResume(
        @RequestParam(name = "nationality", defaultValue = "kazakh") nationality: String
    ): String {
        return documentService.generateBase64(nationality)
    }

    @GetMapping("/findByPersonId/{personId}")
    fun findByPersonId(@PathVariable personId: Long): List<Document> {
        return documentRepository.findByPersonId(personId)
    }

    @PostMapping("/download")
    fun downloadFirstDocumentByPerson(@RequestParam personId: Long): ResponseEntity<Resource> {
        return documentService.downloadFirstDocumentByPersonId(personId)
    }
}
