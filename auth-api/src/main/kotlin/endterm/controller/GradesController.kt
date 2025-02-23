package endterm.controller

import endterm.service.GradesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/grades")
class GradesController(
    val gradesService: GradesService
) {

    @GetMapping("/getGradesByUserId/{userId}")
    fun getGrades(@PathVariable userId:Long,
                  @RequestParam(required = true) year: Int,
                  @RequestParam(required = true) semester: Int): Any? {
        return gradesService.getGradesByUserId(userId, year, semester)
    }

}