package endterm.model.Dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import endterm.component.DateDeserializer
import endterm.component.DateSerializer
import java.time.LocalDate

class Student{

    var personID: Long? = null
    var lastnameEN: String? = null
    var firstnameEN: String? = null
    var groupID: Long? = null
    var GPA: Double? = null
    var groupName: String? = null
    var mobilePhone: String? = null
    var specializationNameEn: String? = null
    var courseNumber: Long? = null
    var education: String? = null
    var adress: String? = null

    @JsonDeserialize(using = DateDeserializer::class)
    @JsonSerialize(using = DateSerializer::class)
    var birthDate: String? = null

}

class UserInfoResponse {
    var student: Student? = null
}

class PersonIdResponse {
    var personID: Long? = null
}