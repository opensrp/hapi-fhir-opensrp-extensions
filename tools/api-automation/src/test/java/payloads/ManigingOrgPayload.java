package payloads;

public class ManigingOrgPayload {

    public static String createManagingOrg()
    {
        return "{\n" +
                "  \"resourceType\": \"Organization\",\n" +
                "  \"text\": {\n" +
                "    \"status\": \"generated\",\n" +
                "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n      \\n      <p>Ministry of Health</p>\\n    \\n    </div>\"\n" +
                "  },\n" +
                "  \"name\": \"Ministry of Health\"\n" +
                "}";
    }
}
