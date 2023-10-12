import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Subscriptions")
public class Subscription
{
    @Id
    @Column(name = "student_id", insertable = false, updatable = false)
    private int studentId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Student student;

    @Id
    @Column(name = "course_id", insertable = false, updatable = false)
    private int courseId;

    @OneToOne(cascade = CascadeType.ALL)
    private Course course;

    @Column(name = "subscription_date")
    private LocalDateTime subscriptionDate;
}
