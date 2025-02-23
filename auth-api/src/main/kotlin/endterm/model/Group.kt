package endterm.model

import javax.persistence.*

@Table(name = "groups")
@Entity
class Group {

    @Id
    @Column
    var id: Long? = null

    @Column
    var name: String? = null

    @Column
    var studentCount: Long? = null

    @Column
    var averageGpa: Double? = null

}