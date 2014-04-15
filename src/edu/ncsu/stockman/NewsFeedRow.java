package edu.ncsu.stockman;
import edu.ncsu.stockman.model.Notification;

public class NewsFeedRow {
	Notification itemName;

    public NewsFeedRow(Notification itemName) {
          super();
          this.itemName = itemName;
    }
    public Notification getItemName() {
          return itemName;
    }
    public void setItemName(Notification itemName) {
          this.itemName = itemName;
    }
}
