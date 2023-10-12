import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.Objects;

public class LinkedPurchaseKey implements Serializable
{
    @Column(name = "student_id", updatable = false, insertable = false)
    private int studentId;

    @Column(name = "course_id", updatable = false, insertable = false)
    private int courseId;

    public LinkedPurchaseKey(int studentId, int courseId)
    {
        this.studentId = studentId;
        this.courseId = courseId;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedPurchaseKey that = (LinkedPurchaseKey) o;
        return studentId == that.studentId && courseId == that.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

}
