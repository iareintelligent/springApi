package com.topher.jcalc.api.db.repository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class UserRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

//    @Test
//    public void whenFindByEmail_thenReturnUser() {
//        //given
//        User torph = new User();
//        torph.setEmail("torph@dwarf.com");
//        entityManager.persist(torph);
//        entityManager.flush();
//        User found = new User();
//
//        //when
//        Optional<User> maybeUser = userRepository.findByEmail(torph.getEmail());
//
//        if (maybeUser)
//            found = maybeUser.get();
//
//        assertThat(found.getId())
//                .isEqualTo(torph.getId());
//
//    }

}