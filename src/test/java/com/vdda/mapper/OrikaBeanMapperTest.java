package com.vdda.mapper;

import com.vdda.jpa.User;
import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


/**
 * Created by francois on 2016-11-02 for
 * vandiedakaf solutions
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class OrikaBeanMapperTest {

    @Autowired
    private OrikaBeanMapper mapper;

    @Test
    public void shouldMapUsertoUserDto() {
        // Given
        User user = new User(123L);

        // When
        User userNew = mapper.map(user, User.class);

        // Then
        assertThat(userNew.getId(), equalTo(123L));
    }

}
