package test.jta.xa;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ItemService {

  private final JmsTemplate jmsTemplate;

  private final ItemRepository itemRepository;

  public ItemService(JmsTemplate jmsTemplate, ItemRepository itemRepository) {
    this.jmsTemplate = jmsTemplate;
    this.itemRepository = itemRepository;
  }

  public Item createItemAndNotify(Item item) {
    Item savedItem = addOrUpdateItem(item);
    notifyItem(savedItem);
    return savedItem;
  }

  public Item editItemAndNotify(Item item) {
    Item savedItem = addOrUpdateItem(item);
    notifyItem(savedItem);
    return savedItem;
  }

  public void deleteItemAndNotify(Long itemId) {
    this.itemRepository.delete(itemId);
    this.jmsTemplate.convertAndSend("itemqueue", "Item Deleted with id "+ itemId);
  }

  public Item getItem(Long itemId) {
    return Optional.ofNullable(this.itemRepository.findOne(itemId)).get()
    .orElseThrow(EntityNotFoundException::new);
  }
  
  public long getItemCount() {
    return this.itemRepository.count();
  }
  
  private Item addOrUpdateItem(Item item) {
    Item savedItem = this.itemRepository.save(item);
    if ("error".equals(item.getName())) {
      throw new RuntimeException("Simulated persistence error");
    }
    return savedItem;
  }

  private void notifyItem(Item item) {
    this.jmsTemplate.convertAndSend("itemqueue", "Item created or updated with name "+ item.getName());
    if (0.00 == item.getCost()) {
      throw new RuntimeException("Simulated queue error");
    }
  }

}
