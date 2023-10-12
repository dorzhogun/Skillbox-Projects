import jakarta.persistence.*;

@Entity
@Table(name = "LinkedPurchaseList")
public class LinkedPurchase
{
    @EmbeddedId
    private LinkedPurchaseKey keyId;

    public LinkedPurchaseKey getKeyId() {
        return keyId;
    }

    public void setKeyId(LinkedPurchaseKey keyId) {
        this.keyId = keyId;
    }

}
