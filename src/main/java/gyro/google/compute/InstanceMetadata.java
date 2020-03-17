package gyro.google.compute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.Metadata;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class InstanceMetadata extends Diffable implements Copyable<Metadata> {

    private String fingerprint;
    private List<InstanceMetadataItem> item;
    private String kind;

    /**
     *
     * @subresource gyro.google.compute.InstanceMetadataItem
     */
    @Updatable
    public List<InstanceMetadataItem> getItem() {
        if (item == null) {
            item = new ArrayList<>();
        }

        return item;
    }

    public void setItem(List<InstanceMetadataItem> item) {
        this.item = item;
    }

    @Output
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Output
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(Metadata model) {
        setItem(
            model.getItems().stream().map(
                i -> {
                    InstanceMetadataItem item = newSubresource(InstanceMetadataItem.class);
                    item.copyFrom(i);
                    return item;
                })
                .collect(Collectors.toList())
        );
        setFingerprint(model.getFingerprint());
        setKind(model.getKind());
    }

    public Metadata copyTo() {
        Metadata metadata = new Metadata();
        metadata.setItems(
            !getItem().isEmpty()
                ? getItem().stream()
                .map(InstanceMetadataItem::copyTo)
                .collect(Collectors.toList())
                : null
        );
        metadata.setFingerprint(getFingerprint());
        metadata.setKind(getKind());

        return metadata;
    }
}
