import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PurchaseList")
public class Purchase
{
    @Id
    @Column(name = "student_name", length = 100)
    private String studentName;

    @Id
    @Column(name = "course_name", length = 100)
    private String courseName;

    @Column(columnDefinition = "int")
    private int price;

    @Column(name = "subscription_date", columnDefinition = "datetime")
    private LocalDateTime subscriptionDate;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDateTime getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(LocalDateTime subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

}
