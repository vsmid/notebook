package hr.yeti.notebook.store;

public class StoreRecord extends Record {

    private long timestamp;
    private String title;
    private String value;

    public StoreRecord(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public StoreRecord(boolean deleted, String title, String value) {
        this.deleted = deleted;
        this.value = value;
    }

    public StoreRecord(boolean deleted, long timestamp, String title, String value) {
        this.deleted = deleted;
        this.timestamp = timestamp;
        this.title = title;
        this.value = value;
    }

    public StoreRecord(long id, boolean deleted, long timestamp, String title, String value) {
        this(deleted, timestamp, title, value);
        super.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
