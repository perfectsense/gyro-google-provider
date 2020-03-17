package gyro.google.compute;

import com.google.api.services.compute.model.Metadata;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class InstanceMetadataItem extends Diffable implements Copyable<Metadata.Items> {

    private String key;
    private String value;

    @Regex("[a-zA-Z0-9-_]+")
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Updatable
    public String getValue() {
        if (value == null) {
            return "";
        }

        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String primaryKey() {
        return getKey();
    }

    @Override
    public void copyFrom(Metadata.Items model) {
        setKey(model.getKey());
        setValue(model.getValue());
    }

    public Metadata.Items copyTo() {
        Metadata.Items item = new Metadata.Items();
        item.setKey(getKey());
        item.setValue(getValue());
        return item;
    }
}
