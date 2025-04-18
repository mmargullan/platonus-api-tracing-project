package endterm.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null

    @Column(unique = true)
    lateinit var login: String

    @Column
    lateinit var password: String

    @Column
    var role: String? = null

    @Column
    var firstName: String? = null

    @Column
    var lastName: String? = null

    @Column
    var fullName: String? = null

    @Column
    var personId: Long? = null

    @Column
    var groupName: String? = null

    @Column
    var gpa: Double? = null

    @Column
    var phone: String? = null

    @Column
    var specializationName: String? = null

    @Column
    var courseNumber: Long? = null

    @Column
    var education: String? = null

    @Column
    var address: String? = null

    @Column
    var birthDate: String? = null

    @Column
    var rating: Int? = null

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    var group: Group? = null

}