import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        List<Purchase> purchaseList = session.createQuery("From Purchase").getResultList();

        List<Student> studentList = session.createQuery("From Student").getResultList();

        List<Course> courseList = session.createQuery("From Course").getResultList();

        for (Purchase p : purchaseList)
        {
            Student student = studentList.stream().filter(s -> s.getName().equals(p.getStudentName())).findFirst().get();
            Course course = courseList.stream().filter(c -> c.getName().equals(p.getCourseName())).findFirst().get();

            LinkedPurchase linkedPurchase = new LinkedPurchase();
            linkedPurchase.setKeyId(new LinkedPurchaseKey(student.getId(), course.getId()));
            session.save(linkedPurchase);
        }

        transaction.commit();
        session.close();
        sessionFactory.close();
    }
}
