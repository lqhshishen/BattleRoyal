import com.example.BattleRoyal.bean.UserTableEntity;
import com.example.BattleRoyal.dao.UserDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


public class LoginTest {
    public static void main(String[] args) {
//        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//
        UserTableEntity user = new UserTableEntity();
        user.setEmail("543376397@qq.com");
        user.setLevel(0);
        user.setUserName("MirageLee");
        user.setPassword("a11223344");
//
//        transaction.commit();
//        session.close();
//        sessionFactory.close();
        System.out.println(UserDao.getInstance().save(user));

    }
}
