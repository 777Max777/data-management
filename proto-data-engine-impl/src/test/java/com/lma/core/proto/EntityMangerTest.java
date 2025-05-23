package com.lma.core.proto;

import com.lma.core.entity.Document;
import com.lma.core.entity.Person;
import com.lma.core.proto.proxy.EntityManagerProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

//@RunWith(SpringRunner.class)
//@DataJpaTest()
//@ActiveProfiles("test")
//@SpringBootTest
//@Sql(value = "file:src/test/resources/sql/baseTransformationEntities.sql")
//@AutoConfigureTestDatabase(replace = NONE)
public class EntityMangerTest {

//    @Autowired
//    private TestEntityManager entityManager;
//    private EntityManager testedEntityManager;

    private static final String TEST_NAME = "Alamerd";
/*
//    @Before
    public void setUp() {
        Set<String> packages = new HashSet<>();
        packages.add("com.lma.core.entity");
        this.testedEntityManager = EntityManagerProxy.proxiedEntityManager(entityManager.getEntityManager(), packages);
        try {
            String transformScript = readFile("src/test/resources/sql/baseTransformationEntities.sql", StandardCharsets.UTF_8);
            testedEntityManager.createNativeQuery(transformScript).executeUpdate();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

//    @After
    public void theEndTest() {
        testedEntityManager.getTransaction().begin();
        testedEntityManager.createNativeQuery("delete from relations where 1 = 1").executeUpdate();
        testedEntityManager.createNativeQuery("delete from instances where 1 = 1").executeUpdate();
        testedEntityManager.createNativeQuery("delete from mappings where 1 = 1").executeUpdate();
        testedEntityManager.createNativeQuery("delete from entities where 1 = 1").executeUpdate();
        testedEntityManager.createNativeQuery("delete from fields where 1 = 1").executeUpdate();
        testedEntityManager.getTransaction().commit();
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get((new File(path)).toURI()));
        return new String(encoded, encoding);
    }

//    @Test
    public void baseSaveInputDataTest() {
        Person person = new Person();
        person.setHeight(BigDecimal.valueOf(187.5));
        person.setSex("MAN");
        person.setWeight(BigDecimal.valueOf(73.33));
        person.setName(TEST_NAME);

        testedEntityManager.persist(person);
        testedEntityManager.getTransaction().commit();

//        Instance instance = (Instance) testedEntityManager.createNativeQuery("select * from persons where name = ?", Person.class)
//                .setParameter(1, TEST_NAME)
//                .getSingleResult();
//
//        assertThat(instance).isInstanceOf(Person.class);
//        assertThat(instance.getName()).isEqualTo(TEST_NAME);
    }

//    @Test
    public void saveInputDataWithRelationsTest() {
        Person person = new Person();
        person.setHeight(BigDecimal.valueOf(187.5));
        person.setSex("MAN");
        person.setWeight(BigDecimal.valueOf(73.33));
        person.setName(TEST_NAME);

        Document doc1 = new Document();
        Document doc2 = new Document();
        Document doc3 = new Document();

        doc1.setName("Документ 1");
        doc2.setName("Документ 2");
        doc3.setName("Документ 3");

        testedEntityManager.persist(doc1);
        testedEntityManager.persist(doc2);
        testedEntityManager.persist(doc3);
        
        List docs = new ArrayList();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);

        person.setDocs(docs);
        testedEntityManager.persist(person);
        testedEntityManager.getTransaction().commit();
    }
    */
}
