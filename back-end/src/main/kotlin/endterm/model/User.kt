package endterm.model

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
    var firstName: String? = null

    @Column
    var lastName: String? = null

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

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    var group: Group? = null

}