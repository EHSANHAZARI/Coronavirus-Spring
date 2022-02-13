package hazariGroup.coronavirustracker.services.services;

import hazariGroup.coronavirustracker.Models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service  // @Service to indicate that they're holding the business logic.
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * * ") // when you want some functionality getting repeated by a schedule
    public void FetchVirusData() throws IOException, InterruptedException { // this method fetch the data that have been provided by uri
        List<LocationStats> newStats= new ArrayList<>(); // we make newStats because we want our application works while we are performing some other task without any delay
        // This is showing how we request to get the information in the specific uri
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        HttpResponse<String>httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvreader = new StringReader(httpResponse.body());
        // Apache common csv provides different method for reading the file we are using default here 
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvreader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            // we are getting the information with these specific title and store them in locationStat
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
            System.out.println(locationStat);
            newStats.add(locationStat);
        }
        this.allStats = newStats;

    }
}
