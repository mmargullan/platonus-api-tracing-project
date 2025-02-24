package endterm.model

import javax.persistence.*

@Entity
@Table(name = "docs")
class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Int? = null

    @Column
    var type: String? = null

    @Column(nullable = false)
    var personId: Long? = null

    @Column
    var fileLink: String? = null

}