package com.lma.core.proto;

import com.lma.core.entity.Person;
import com.lma.core.proto.entity.Instance;
import com.lma.core.proto.repository.EntityRepository;
import com.lma.core.proto.repository.InstanceRepository;
import com.lma.core.proto.transformation.BaseTransformation;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = NONE)
//@Disabled
public class TransformationTest {
    
    @Autowired
    private BaseTransformation baseTransformation;
    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private InstanceRepository instanceRepository;

    @Before
    public void setUp() {
    }

    @After
    public void theEndTest() {
        instanceRepository.deleteAll();
        entityRepository.deleteAll();
    }

//    @Test
//    @Sql(value = "file:src/test/resources/sql/baseTransformationEntities.sql")
    public void testCollectProtoData() {
        Person person = new Person();
        person.setHeight(BigDecimal.valueOf(187.5));
        person.setSex("MAN");
        person.setWeight(BigDecimal.valueOf(73.33));
        person.setName("Vasya");

        Instance instance = baseTransformation.collectProtoData(person);
        instanceRepository.save(instance);

        List<Instance> addedObjLst = instanceRepository.findAll();
        assertThat(addedObjLst != null).isTrue();

        Instance detachedInstance = addedObjLst.get(0);
        assertThat(detachedInstance.getEntity().getId()).isEqualTo(1L);
        assertThat(detachedInstance.getName()).isEqualTo(person.getName());
    }




}
