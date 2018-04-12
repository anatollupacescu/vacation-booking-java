package com.example;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class HolidaySchedulerTest extends SimpleScenarioTest<HolidaySchedulerState> {

  @Test
  public void notAcceptRequestsFromNonExistentUser() {
    given()
        .no_users_have_been_created();

    when().user_$_requests_valid_holiday("Jora");

    then().error_response_is_returned("User 'Jora' not found");
  }

  @Test
  public void userCanSubmitValidHolidayRequest() {
    given()
        .a_user_exist("Bob");

    when().user_$_requests_valid_holiday("Bob");

    then().there_are_$_pending_requests_from_user_$(1, "Bob");
  }

  @Test
  public void userCanNotSubmitRequestsWithPastDates() {
    given()
        .a_user_exist("Bob");

    when().user_$_requests_holiday_in_the_past("Bob");

    then()
        .there_are_$_pending_requests_from_user_$(0, "Bob").and()
        .error_response_is_returned("The dates can not be in the past");
  }

  @Test
  public void managerCanAcceptPendingRequest() {
    given()
        .a_user_exist("Bob").and()
        .a_manager_exists("Jane");

    when()
        .user_$_requests_valid_holiday("Bob")
        .manager_$_accepts_request_from_$("Jane", "Bob");

    then()
        .there_are_$_pending_requests_from_user_$(0, "Bob").and()
        .there_are_$_accepted_requests_for_user_$(1, "Bob");
  }
}
