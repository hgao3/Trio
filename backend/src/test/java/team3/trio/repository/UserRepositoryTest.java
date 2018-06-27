package team3.trio.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import team3.trio.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository users;

    private User hgao = new User("Hugh", "Gao", "hgao3@bu.edu", true);
    private User mchen = new User("Miffy", "Chen", "mchen15@bu.edu", false);
    private User mhachey = new User("Michael", "Hachey", "mhachey@bu.edu", true);
    private User nehap = new User("Neha", "Pawar", "nehap86@bu.edu", false);
    private User japrker = new User("Jefferson", "Parker", "japarker@bu.edu", false);

    @Test
    public void testTotalSize() throws Exception {
        // Search for specific User in Database according to lastname
        List<User> userList = users.findAll();
        Assert.assertTrue(userList.size()==5);
    }
    
    @Test
    public void testAddAndRemoveOne() throws Exception {
        // Search for specific User in Database according to lastname
    	User newuser = new User("new", "user", "new@bu.edu", false);
    	users.save(newuser);
        List<User> userList = users.findAll();
        assertThat(userList.size()==6);
        
    	users.delete(newuser);
        userList = users.findAll();
        Assert.assertTrue(userList.size()==5);
    }
    
    @Test
    public void testFindByLastName() throws Exception {
        // Search for specific User in Database according to lastname
        List<User> usersWithLastNameGao = users.findByLastName("Gao");
        assertThat(usersWithLastNameGao, contains(hgao));
    }


    @Test
    public void testFindByFirstName() throws Exception {
        // Search for specific User in Database according to firstname
        List<User> usersWithFirstNameMiffy = users.findByFirstName("Miffy");
        assertThat(usersWithFirstNameMiffy, contains(mchen));
    }

}
