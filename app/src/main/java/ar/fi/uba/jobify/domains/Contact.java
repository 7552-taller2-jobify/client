package ar.fi.uba.jobify.domains;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ar.fi.uba.jobify.exceptions.BusinessException;
import ar.fi.uba.jobify.utils.DateUtils;
import ar.fi.uba.jobify.utils.FieldValidator;

/**
 * Created by smpiano on 9/28/16.
 */
public class Contact {

    private long id;
    private String name;
    private String lastName;
    private String company;
    private String cuil;
    private String address;
    private String phoneNumber;
    private String email;
    private String sellerType;

    private double lon;
    private double lat;
    private String thumbnail;
    private String avatar;


    private Date dateCreated;
    private Date lastModified;
    private double distance;
    private Date visited;

    public Contact(long id) {
        super();
        this.id= id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSellerType() {
        return sellerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setAddress(String address) { this.address = address; }

    public String getAddress() { return address; }

    public long getId() { return id; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getAvatar() { return avatar; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFullName() {
        if ((!this.name.isEmpty()) && (!this.name.isEmpty())) {
            return this.lastName +", "+ this.name;
        } else {
            if (!this.name.isEmpty()){
                return this.name;
            } else {
                return this.lastName;
            }
        }
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Date getVisited() {
        return visited;
    }

    public void setVisited(Date visited) {
        this.visited = visited;
    }

    public static Contact fromJson(JSONObject json) {
        Contact contact = null;
        try {
            contact = new Contact(json.getLong("id"));
            contact.setName(json.getString("name"));
            contact.setLastName(json.getString("lastname"));
            contact.setCompany(json.getString("company"));
            contact.setAddress(json.getString("address"));
            contact.setThumbnail(json.getString("thumbnail"));

            contact.setCuil(json.getString("cuil"));
            if (!json.isNull("lat")) contact.setLat(json.getDouble("lat"));
            if (!json.isNull("lon")) contact.setLon(json.getDouble("lon"));
            contact.setEmail(json.getString("email"));
            contact.setAvatar(json.getString("avatar"));
            contact.setPhoneNumber(json.getString("phone_number"));

            String dateCreatedStr = json.getString("date_created");
            Date dateCreated = null;
            if (FieldValidator.isValid(dateCreatedStr)) dateCreated = DateUtils.parseDate(dateCreatedStr);
            contact.setDateCreated(dateCreated);
            String lastModifiedStr = json.getString("last_modified");
            Date lastModified = null;
            if (FieldValidator.isValid(lastModifiedStr)) lastModified = DateUtils.parseDate(lastModifiedStr);
            contact.setLastModified(lastModified);
            if (json.toString().contains("distance")) contact.setDistance(json.getDouble("distance"));
            if (json.toString().contains("visited")){
                String visitedStr = json.getString("visited");
                Date visited = null;
                if (FieldValidator.isValid(visitedStr)) visited = DateUtils.parseDate(visitedStr);
                contact.setVisited(visited);
            }


        } catch (JSONException e) {
            throw new BusinessException("Error parsing Contact.",e);
        }
        return contact;
    }
}
