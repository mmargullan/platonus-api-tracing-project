package endterm.controller

import endterm.service.DocumentService
import endterm.service.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService
) {

    @PostMapping("/generate")
    fun generateResume(@RequestParam nationality: String): String {
        return documentService.generateBase64(nationality)
    }

}