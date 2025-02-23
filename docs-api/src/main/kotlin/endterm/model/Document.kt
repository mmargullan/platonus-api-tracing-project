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

    @Column(columnDefinition = "TEXT", length = 1010485760)
    var base64code: String? = null

    @Column(nullable = false)
    var personId: Long? = null

}