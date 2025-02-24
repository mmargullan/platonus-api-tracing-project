package endterm.controller

import endterm.model.Document
import endterm.repository.DocumentRepository
import endterm.service.DocumentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/document")
class DocumentController(
    private val documentRepository: DocumentRepository,
    private val documentService: DocumentService
) {

    @PostMapping("/generate")
    fun generateResume(@RequestParam nationality: String): String {
        return documentService.generateBase64(nationality)
    }

    @GetMapping("/findByPersonId/{personId}")
    fun findByPersonId(@PathVariable personId: Long): List<Document> {
        return documentRepository.findByPersonId(personId)
    }

}