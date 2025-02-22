package endterm.model.Dto

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

}

class UserInfoResponse {
    var student: Student? = null
}

class PersonIdResponse {
    var personID: Long? = null
}