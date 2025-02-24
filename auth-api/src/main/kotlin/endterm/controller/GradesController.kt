package endterm.controller

import endterm.service.GradesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/grades")
class GradesController(
    val gradesService: GradesService
) {

    @GetMapping("/getGrades")
    fun getGrades(@RequestParam(required = true) year: Int,
                  @RequestParam(required = true) semester: Int): Any? {
        return gradesService.getGradesByUserId(year, semester)
    }

}