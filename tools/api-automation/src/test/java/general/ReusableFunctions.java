package general;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;


public class ReusableFunctions {
    public static RequestSpecification REQUEST;

    public ReusableFunctions() {
    }

    public static ArrayList responseList(String key) {
        return (ArrayList)((ValidatableResponse) general.EnvGlobals.response.then()).extract().path(key, new String[0]);
    }

    public static int getResponseLength() {
        return (Integer) general.EnvGlobals.response.body().path("list.size()", new String[0]);
    }

    public static void printResponse() {
        if (general.EnvGlobals.response != null) {
            System.out.println(general.EnvGlobals.response.getBody().asString());
        }

    }

    public static String getResponse() {
        return general.EnvGlobals.response != null ? general.EnvGlobals.response.getBody().asString() : null;
    }

    public static int getResponseLengthByKey(String Key) {
        return (Integer) general.EnvGlobals.response.body().path(Key, new String[0]);
    }

    private static JSONArray sortApiResponse(JSONArray jsonArr, final String sortBy, boolean sortOrder) {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList();

        for(int i = 0; i < jsonArr.length(); ++i) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }

        final Boolean SORT_ORDER = sortOrder;
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            public int compare(JSONObject a, JSONObject b) {
                Integer valA = new Integer(0);
                Integer valB = new Integer(0);

                try {
                    valA = (Integer)a.get(sortBy);
                    valB = (Integer)b.get(sortBy);
                } catch (JSONException var6) {
                }

                return SORT_ORDER ? valA.compareTo(valB) : -valA.compareTo(valB);
            }
        });

        for(int i = 0; i < jsonArr.length(); ++i) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        return sortedJsonArray;
    }

    public static void givenHeadersWithBasicAuth(String username, String password, String Cookie) {
        System.out.println(username + "" + password);
        contentType("application/json");
        general.EnvGlobals.requestSpecification = REQUEST.given().auth().basic(username, password).cookie(Cookie);
    }

    public static String getResponsePath(String key) {
        return general.EnvGlobals.response.getBody().path(key, new String[0]).toString();
    }

    public static int getLength(String Path) {
        return (Integer) general.EnvGlobals.response.body().path(Path, new String[0]);
    }

    public static void verifySchema(String jsonFile) {
        ((ValidatableResponse)((ValidatableResponse)((ValidatableResponse) general.EnvGlobals.response.then()).log().all()).assertThat()).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonFile), new Matcher[0]);
    }

    public static JSONArray getResponseJson(String... params) {
        JsonPath jsonPathEvaluator = general.EnvGlobals.response.jsonPath();
        JSONArray jArray = new JSONArray();
        ArrayList<Object> list1 = (ArrayList)jsonPathEvaluator.get(params[0]);
        ArrayList<Object> list2 = (ArrayList)jsonPathEvaluator.get(params[1]);

        for(int i = 0; i < getResponseLength(); ++i) {
            JSONObject obj = new JSONObject();

            for(int j = 0; j < params.length; ++j) {
                if (j == 0) {
                    obj.put(params[j], list1.get(i));
                } else {
                    obj.put(params[j], list2.get(i));
                }
            }

            jArray.put(obj);
        }

        return sortApiResponse(jArray, "id", true);
    }

    private static void contentType(String contentType) {
        REQUEST = RestAssured.given().contentType(contentType);
    }

    public static void given() {
        contentType("application/json");
       general.EnvGlobals.requestSpecification = REQUEST.given();
    }

    public static void givenFormData(Map<String, String> formData) {
        contentType("multipart/form-data");
        Iterator it = formData.entrySet().iterator();

        while(it.hasNext()) {
            Entry<String, String> pair = (Entry)it.next();
            general.EnvGlobals.requestSpecification = REQUEST.given().multiPart((String)pair.getKey(), (String)pair.getValue());
            it.remove();
        }

    }

    public static void givenHeaders(Map<String, String> headers) {
        contentType("application/json");
        general.EnvGlobals.requestSpecification = REQUEST.given().headers(headers);
    }

    public static void givenHeaderPayload(Map<String, String> headers, String payload) {
        contentType("application/json");
        general.EnvGlobals.requestSpecification = REQUEST.given();
        if (headers == null) {
            general.EnvGlobals.requestSpecification = REQUEST.given().body(payload);
        } else if (payload == null) {
            general.EnvGlobals.requestSpecification = REQUEST.given().headers(headers);
        } else {
            general.EnvGlobals.requestSpecification = REQUEST.given().headers(headers).body(payload);
        }

    }

    public static void givenHeaderFormData(Map<String, String> headers, Map<String, String> formData) {
        contentType("multipart/form-data");
        Iterator<Entry<String, String>> it = formData.entrySet().iterator();
        Entry pair;
        if (headers == null) {
            general.EnvGlobals.requestSpecification = REQUEST.given();

            while(it.hasNext()) {
                pair = (Entry)it.next();
                general.EnvGlobals.requestSpecification = REQUEST.given().multiPart((String)pair.getKey(), (String)pair.getValue());
                it.remove();
            }
        } else {
            general.EnvGlobals.requestSpecification = REQUEST.given().headers(headers);

            while(it.hasNext()) {
                pair = (Entry)it.next();
                general.EnvGlobals.requestSpecification = REQUEST.given().multiPart((String)pair.getKey(), pair.getValue());
                it.remove();
            }
        }

    }

    public static void givenHeaderFormDataWithParam(Map<String, String> headers, Map<String, String> params, Map<String, String> formData) {
        contentType("multipart/form-data");
        Iterator<Entry<String, String>> it = formData.entrySet().iterator();
        Entry pair;
        if (headers == null) {
            general.EnvGlobals.requestSpecification = REQUEST.given();

            while(it.hasNext()) {
                pair = (Entry)it.next();
               general.EnvGlobals.requestSpecification = REQUEST.given().multiPart((String)pair.getKey(), (String)pair.getValue());
                it.remove();
            }
        } else {
            general.EnvGlobals.requestSpecification = REQUEST.given().queryParams(params).headers(headers);

            while(it.hasNext()) {
                pair = (Entry)it.next();
                general.EnvGlobals.requestSpecification = REQUEST.given().multiPart((String)pair.getKey(), pair.getValue());
                it.remove();
            }
        }

    }

    public static void whenFunction(String requestType, String endPoint) {
        byte var3 = -1;
        switch(requestType.hashCode()) {
            case -1335458389:
                if (requestType.equals("delete")) {
                    var3 = 2;
                }
                break;
            case 102230:
                if (requestType.equals("get")) {
                    var3 = 1;
                }
                break;
            case 111375:
                if (requestType.equals("put")) {
                    var3 = 3;
                }
                break;
            case 3446944:
                if (requestType.equals("post")) {
                    var3 = 0;
                }
                break;
            case 106438728:
                if (requestType.equals("patch")) {
                    var3 = 4;
                }
        }

        switch(var3) {
            case 0:
               general.EnvGlobals.response = (Response)((RequestSpecification) general.EnvGlobals.requestSpecification.when().log().all()).post(endPoint, new Object[0]);
                break;
            case 1:
                general.EnvGlobals.response = (Response)((RequestSpecification) general.EnvGlobals.requestSpecification.when().log().all()).get(endPoint, new Object[0]);
                break;
            case 2:
                general.EnvGlobals.response = (Response)((RequestSpecification) general.EnvGlobals.requestSpecification.when().log().all()).delete(endPoint, new Object[0]);
                break;
            case 3:
                general.EnvGlobals.response = (Response)((RequestSpecification) general.EnvGlobals.requestSpecification.when().log().all()).put(endPoint, new Object[0]);
                break;
            case 4:
                general.EnvGlobals.response = (Response)((RequestSpecification) general.EnvGlobals.requestSpecification.when().log().all()).patch(endPoint, new Object[0]);
        }

    }

    public static void thenFunction(int statusCode) {
        ((ValidatableResponse)((ValidatableResponse) general.EnvGlobals.response.then()).log().all()).statusCode(statusCode);
    }

    public static void thenFunction(int statusCode, int statusCode2) {
        ((ValidatableResponse)((ValidatableResponse) general.EnvGlobals.response.then()).log().all()).statusCode(anyOf(is(statusCode),is(statusCode2)));
    }

    public static void thenObjectmatch(String path, String matchers) {
        ((ValidatableResponse) general.EnvGlobals.response.then()).body(path, Matchers.hasItem(matchers), new Object[0]);
    }

    public static <K, V> Map<K, V> headers(Object... keyValues) {
        Map<K, V> map = new HashMap();

        for(int index = 0; index < keyValues.length / 2; ++index) {
            map.put((K)keyValues[index * 2], (V)keyValues[index * 2 + 1]);
        }

        return map;
    }

    public static <K, V> Map<K, V> form_data(Object... keyValues) {
        Map<K, V> map = new HashMap();

        for(int index = 0; index < keyValues.length / 2; ++index) {
            map.put((K)keyValues[index * 2], (V)keyValues[index * 2 + 1]);
        }

        return map;
    }

    public static <K, V> Map<K, V> params(Object... keyValues) {
        Map<K, V> map = new HashMap();

        for(int index = 0; index < keyValues.length / 2; ++index) {
            map.put((K)keyValues[index * 2], (V)keyValues[index * 2 + 1]);
        }

        return map;
    }

    public static void compareFile(String apiResponse, String jsonFile, String[] ignoreFields) {
        JSONParser jsonParser = new JSONParser();
        ObjectMapper mapper = new ObjectMapper();

        try {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject)jsonParser.parse(new FileReader(jsonFile));
            String expectedResponse = jsonObject.toString();
            general.FlatMapUtil.patterns = ignoreFields;
            apiResponse = general.FlatMapUtil.transformJson(apiResponse);
            expectedResponse = general.FlatMapUtil.transformJson(expectedResponse);
            Map<String, Object> mapActual = (Map)mapper.readValue(apiResponse, Map.class);
            Map<String, Object> mapExpected = (Map)mapper.readValue(expectedResponse, Map.class);
            Map<String, Object> actualFlatMap = general.FlatMapUtil.flatten(mapActual);
            Map<String, Object> ExpectedFlatMap = FlatMapUtil.flatten(mapExpected);
            MapDifference<String, Object> difference = Maps.difference(actualFlatMap, ExpectedFlatMap);
            System.out.println("Entries only on left\n--------------------------");
            difference.entriesOnlyOnLeft().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            System.out.println("\n\nEntries only on right\n--------------------------");
            difference.entriesOnlyOnRight().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            System.out.println("\n\nEntries differing\n--------------------------");
            difference.entriesDiffering().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            System.out.println("\n\nEntries differing\n--------------------------");
            difference.entriesDiffering().forEach((key, value) -> {
                general.EnvGlobals.difference.append(key + ": " + value);
            });
            System.out.println("\n\nEntries in common\n--------------------------");
            difference.entriesInCommon().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            Assert.assertEquals(difference.entriesDiffering().size(), 0);
            Assert.assertEquals(difference.entriesOnlyOnLeft().size(), difference.entriesOnlyOnRight().size());
        } catch (ParseException | IOException var12) {
            var12.printStackTrace();
        }

    }

    public static void givenParamHeader(Map<String, String> params, Map<String, String> headers) {
        contentType("application/json");
        general.EnvGlobals.requestSpecification = REQUEST.given().queryParams(params).headers(headers);
    }

    public static String Allocation(String EmployeeStartDate, String EmployeeEndDate, int Year, int Month, int AllocationPercent, int SpecialHolidays) throws ParseException, java.text.ParseException {
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        int EmpWorkingDays = WorkingDays(DateFor.parse(EmployeeStartDate), DateFor.parse(EmployeeEndDate), SpecialHolidays);
        int TotalWorkingDaysInMonth = WorkingDayinMonth(Year, Month, SpecialHolidays);
        double all = EmployeeAllocation((double)EmpWorkingDays, (double)TotalWorkingDaysInMonth, (double)AllocationPercent);
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(all);
    }

    private static int WorkingDays(Date startDate, Date endDate, int SpecialHolidays) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int workDays = 0;
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 1;
        } else {
            if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
                startCal.setTime(endDate);
                endCal.setTime(startDate);
            }

            do {
                if (startCal.get(7) != 7 && startCal.get(7) != 1) {
                    ++workDays;
                }

                startCal.add(5, 1);
            } while(startCal.getTimeInMillis() <= endCal.getTimeInMillis());

            if (SpecialHolidays != 0) {
                return workDays - SpecialHolidays;
            } else {
                return workDays;
            }
        }
    }

    private static double EmployeeAllocation(double WorkingDays, double TotalWorkingDayinMonth, double employeeAllocation) {
        double Allocation = 0.0D;
        Allocation = WorkingDays * (employeeAllocation / 100.0D) / TotalWorkingDayinMonth;
        return Allocation;
    }

    private static int WorkingDayinMonth(int year, int month, int specialHolidays) throws ParseException, java.text.ParseException {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        LocalDate last = yearMonth.atEndOfMonth();
        String StartDate = firstOfMonth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String EndDate = last.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Date Start = (new SimpleDateFormat("dd/MM/yyyy")).parse(StartDate);
        Date End = (new SimpleDateFormat("dd/MM/yyyy")).parse(EndDate);
        int count = WorkingDays(Start, End, specialHolidays);
        return count;
    }
}
