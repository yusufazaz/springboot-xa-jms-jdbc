package test.jta.xa;

import java.net.URI;
import java.util.Collections;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ItemControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  private URI uri;
  
  @After
  public void cleanup() throws Exception {
    restTemplate.delete(uri);
  }

  @Test
  public void testPostItem() {
      Item item = new Item("Test",10.0);
      uri = restTemplate.postForLocation("/api/items/", item, Collections.EMPTY_MAP);
      Item savedItem = this.restTemplate.getForEntity(
          uri, Item.class).getBody();
      assertThat( savedItem.getId() , notNullValue() );
      assertThat( savedItem.getName() , is(item.getName()) );
      assertThat( savedItem.getCost() , equalTo(item.getCost()) );
  }

  @Test
  public void testUpdateItem() {
    Item item = new Item("Test",10.0);
    uri = restTemplate.postForLocation("/api/items/", item, Collections.EMPTY_MAP);
    Item savedItem = this.restTemplate.getForEntity(uri, Item.class).getBody();
    assertThat( savedItem , notNullValue() );
    savedItem.setCost(200.0);
    restTemplate.put(uri.getPath(), savedItem);
    Item updatedItem = this.restTemplate.getForEntity(uri, Item.class).getBody();
    assertThat( updatedItem.getName() , is(item.getName()) );
    assertThat( updatedItem.getCost() , equalTo(savedItem.getCost()) );
  }

  @Test
  public void testXATransaction() {
    Item item = new Item("Test",10.0);
    uri = restTemplate.postForLocation("/api/items/", item, Collections.EMPTY_MAP);
    Long itemCount = this.restTemplate.getForEntity("/api/items/count", Long.class).getBody();
    assertThat( itemCount ,is(1L) );
    //JMS failure
    restTemplate.postForLocation("/api/items/", new Item("Test",0.0), Collections.EMPTY_MAP);
    this.restTemplate.getForEntity("/api/items/count", Long.class).getBody();
    assertThat( itemCount ,is(1L) );
    //DB failure
    restTemplate.postForLocation("/api/items/", new Item("error",10.0), Collections.EMPTY_MAP);
    this.restTemplate.getForEntity("/api/items/count", Long.class).getBody();
    assertThat( itemCount ,is(1L) );

  }

}
