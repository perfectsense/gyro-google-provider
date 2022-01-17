package gyro.google.compute;

import gyro.core.resource.Diffable;

public class SubnetworkSecondaryRange extends Diffable {

    private String ipCidrRange;
    private String name;

    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String primaryKey() {
        return getName();
    }

}
