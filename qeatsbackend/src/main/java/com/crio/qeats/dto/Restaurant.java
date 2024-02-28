
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: CRIO_TASK_MODULE_SERIALIZATION
//  Implement Restaurant class.
// Complete the class such that it produces the following JSON during serialization.
// {
//  "restaurantId": "10",
//  "name": "A2B",
//  "city": "Hsr Layout",
//  "imageUrl": "www.google.com",
//  "latitude": 20.027,
//  "longitude": 30.0,
//  "opensAt": "18:00",
//  "closesAt": "23:00",
//  "attributes": [
//    "Tamil",
//    "South Indian"
//  ]
// }


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {

  
  @JsonIgnore
  private String id;

  @JsonProperty("restaurantId")
  private String restaurantId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("city")
  private String city;

  @JsonProperty("imageUrl")
  private String  imageUrl;

  @JsonProperty("latitude")
  private double latitude;

  @JsonProperty("longitude")
  private double longitude;

  @JsonProperty("opensAt")
  private String opensAt;

  @JsonProperty("closesAt")
  private String closeAt;

  @JsonProperty("attributes")
  private List<String> attributes;

  public void setLatitude(double latitude) {
    this.latitude= latitude;
  }
  public void setLongitude(double longitude) {
    this.longitude= longitude;
  }




}

