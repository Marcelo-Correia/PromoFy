package up.edu.br.promofy.models;

import android.location.Geocoder;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import up.edu.br.promofy.helpers.ApplicationHelper;

public class Promotion {

    public String uid;
    public String userUId;
    public String imagePath;
    public String description;
    public float originalPrice;
    public float promotionalPrice;
    public Date createdAt;
    public Location location;

    public Promotion() {
    }

    public Promotion(String userUId, String imagePath, String description, float originalPrice, float promotionalPrice, android.location.Location location)
    {
        uid = UUID.randomUUID().toString();
        createdAt = new Date();

        this.userUId = userUId;
        this.imagePath = imagePath;
        this.description = description;
        this.originalPrice = originalPrice;
        this.promotionalPrice = promotionalPrice;
        this.location = new Location(location.getLatitude(), location.getLongitude());
    }

    public String getUid() {
        return uid;
    }

    public String getUserUId() {
        return userUId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return description;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public float getPromotionalPrice() {
        return promotionalPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Location getLocation() {
        return location;
    }

    public String getLocationName() {
        Geocoder geocoder = new Geocoder(ApplicationHelper.getContext(), Locale.getDefault());

        try {
            return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Promotion)) {
            return false;
        }

        Promotion temp = (Promotion) obj;

        return uid.equals(temp.uid);
    }
}
