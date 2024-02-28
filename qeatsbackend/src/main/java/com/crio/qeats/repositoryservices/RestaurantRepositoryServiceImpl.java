/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import ch.hsr.geohash.GeoHash;
import redis.clients.jedis.Jedis;
import com.crio.qeats.configs.RedisConfiguration;
// import com.crio.qeats.configs.RedisConfiguration;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;
import com.crio.qeats.utils.GeoLocation;
import com.crio.qeats.utils.GeoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Provider;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@Component
@Primary
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {


  @Autowired
  private RestaurantRepository restaurantRepository;


  @Autowired
  private RedisConfiguration redisConfiguration;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ModelMapper modelMapperProvider;

  private static final Logger logger = LoggerFactory.getLogger(RestaurantRepositoryServiceImpl.class);
// 

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objectives:
  // 1. Implement findAllRestaurantsCloseby.
  // 2. Remember to keep the precision of GeoHash in mind while using it as a key.
  // Check RestaurantRepositoryService.java file for the interface contract.
  // public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
  //     Double longitude, LocalTime currentTime, Double servingRadiusInKms) {

  //   List<Restaurant> restaurants= new ArrayList<>();
  //   List<RestaurantEntity> restaurantEntities;

  //  long start= System.currentTimeMillis();

  //   restaurantEntities=  restaurantRepository.findAll();
  //   long stop= System.currentTimeMillis();

  //   System.out.println("time elapsed in respository :"+ (start-stop));

  //   for(RestaurantEntity entity: restaurantEntities)
  //   {
  //     if(isOpenNow(currentTime, entity))
  //     {
  //       if(isRestaurantCloseByAndOpen(entity, currentTime, latitude, longitude, servingRadiusInKms))
  //       {
  //         // Restaurant newRestaurant= new Restaurant(entity.getId(), entity.getRestaurantId(), entity.getName(), entity.getCity(), entity.getImageUrl(), entity.getLatitude(), entity.getLongitude(), entity.getOpensAt(), entity.getClosesAt(), entity.getAttributes());
  //         restaurants.add(modelMapperProvider.map(entity, Restaurant.class));
  //        }
  //     }
      
  //   }
  //   logger.info("getRestaurants returned {}", restaurants.size());
  //   return restaurants;
 
// }
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurants = new ArrayList<>();
    // TODO: CRIO_TASK_MODULE_REDIS
    // We want to use cache to speed things up. Write methods that perform the same functionality,
    // but using the cache if it is present and reachable.
    // Remember, you must ensure that if cache is not present, the queries are directed at the
    // database instead.

    if (redisConfiguration.isCacheAvailable()) {
      restaurants = findAllRestaurantsFromCache(latitude, longitude, currentTime, servingRadiusInKms);
    } else {
      restaurants = findAllRestaurantsFromDB(latitude, longitude, currentTime, servingRadiusInKms);
    }

    return restaurants;
  }



  public List<Restaurant> findAllRestaurantsFromDB(Double latitude, Double longitude, LocalTime currentTime, Double servingRadiusInKms)
  {
       List<Restaurant> restaurants= new ArrayList<>();
    List<RestaurantEntity> restaurantEntities;

  //  long start= System.currentTimeMillis();

    restaurantEntities=  restaurantRepository.findAll();
    // long stop= System.currentTimeMillis();

    // System.out.println("time elapsed in respository :"+ (start-stop));

    for(RestaurantEntity entity: restaurantEntities)
    {
      if(isOpenNow(currentTime, entity))
      {
        if(isRestaurantCloseByAndOpen(entity, currentTime, latitude, longitude, servingRadiusInKms))
        {
          // Restaurant newRestaurant= new Restaurant(entity.getId(), entity.getRestaurantId(), entity.getName(), entity.getCity(), entity.getImageUrl(), entity.getLatitude(), entity.getLongitude(), entity.getOpensAt(), entity.getClosesAt(), entity.getAttributes());
          restaurants.add(modelMapperProvider.map(entity, Restaurant.class));
         }
      }
      
    }
    logger.info("getRestaurants returned {}", restaurants.size());
    return restaurants;
 
    
  }


  public List<Restaurant> findAllRestaurantsFromCache(Double latitude, Double longitude, LocalTime currentTime, Double servingRadiusInKms)
  {
  
    List<Restaurant> restaurantList = new ArrayList<>();

    GeoLocation geoLocation = new GeoLocation(latitude, longitude);
    GeoHash geoHash = GeoHash.withCharacterPrecision(geoLocation.getLatitude(), geoLocation.getLongitude(), 7);

    try (Jedis jedis = redisConfiguration.getJedisPool().getResource()) {
      String jsonStringFromCache = jedis.get(geoHash.toBase32());

      if (jsonStringFromCache == null) {
        // Cache needs to be updated.
        String createdJsonString = "";
        try {
          restaurantList = findAllRestaurantsFromDB(geoLocation.getLatitude(), geoLocation.getLongitude(),
              currentTime, servingRadiusInKms);
          createdJsonString = new ObjectMapper().writeValueAsString(restaurantList);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
        jedis.setex(geoHash.toBase32(), GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS, createdJsonString);
      } else {
        try {
          restaurantList = new ObjectMapper().readValue(jsonStringFromCache, new TypeReference<List<Restaurant>>() {
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return restaurantList;
  }


      

  


  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objective:
  // 1. Check if a restaurant is nearby and open. If so, it is a candidate to be returned.
  // NOTE: How far exactly is "nearby"?

  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
      LocalTime currentTime, Double latitude, Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return GeoUtils.findDistanceInKm(latitude, longitude,
          restaurantEntity.getLatitude(), restaurantEntity.getLongitude())
          < servingRadiusInKms;
    }

    return false;
  }



}

