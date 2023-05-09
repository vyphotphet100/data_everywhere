package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.entity.DatasetCategoryEntity;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.entity.DatasetItemEntity;
import com.caovy2001.data_everywhere.repository.DatasetCategoryRepository;
import com.caovy2001.data_everywhere.repository.DatasetCollectionRepository;
import com.caovy2001.data_everywhere.repository.DatasetItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.*;

@RestController
@RequestMapping("/tool")
public class Tool {
    @Autowired
    private DatasetCategoryRepository datasetCategoryRepository;

    @Autowired
    private DatasetCollectionRepository datasetCollectionRepository;

    @Autowired
    private DatasetItemRepository datasetItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/add_fake_dataset_category")
    public boolean addFakeDatasetCategory(@RequestBody DatasetCategoryEntity datasetCategory) {
        try {
            datasetCategoryRepository.insert(datasetCategory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/add_fake_dataset_collection")
    public boolean addFakeDatasetCollection() {
        try {
            String shortDes = "<p>The Internet (or internet) is a global system of interconnected computer networks that uses the Internet protocol suite (TCP/IP) to communicate between networks and devices. It is a network of networks that consists of private, public, academic, business, and government networks of local to global scope, linked by a broad array of electronic, wireless, and optical networking technologies. The Internet carries a vast range of information resources and services, such as the interlinked hypertext documents and applications of the World Wide Web (WWW), electronic mail, telephony, and file sharing.</p>";
            String des = "<h2>Context</h2> <p>The Internet (or internet) is a global system of interconnected computer networks that uses the Internet protocol suite (TCP/IP) to communicate between networks and devices. It is a network of networks that consists of private, public, academic, business, and government networks of local to global scope, linked by a broad array of electronic, wireless, and optical networking technologies. The Internet carries a vast range of information resources and services, such as the interlinked hypertext documents and applications of the World Wide Web (WWW), electronic mail, telephony, and file sharing.</p> <h2>Content</h2> <p>The following dataset has information about internet users from 1980-2020. Details about the columns are as follows:</p> <ol> <li>Entity - Contains the name of the countries and the regions.</li> <li>Code - Information about country code and where code has the value 'Region', it denotes division by grouping various countries. </li> <li>Year - Year from 1980-2020</li> <li>Cellular Subscription - Mobile phone subscriptions per 100 people. This number can get over 100 when the average person has more than one subscription to a mobile service.</li> <li>Internet Users(%) - The share of the population that is accessing the internet for all countries of the world. </li> <li>No. of Internet Users - Number of people using the Internet in every country.</li> <li>Broadband Subscription - The number of fixed broadband subscriptions per 100 people. This refers to fixed subscriptions to high-speed access to the public Internet (a TCP/IP connection), at downstream speeds equal to, or greater than, 256 kbit/s.</li> </ol>";
            String preview = "/dataset_collection/64076f2a9ede6ac346c8da3f/preview.pdf";
            Map<String, List<DatasetCollectionEntity>> cateMapDataset = new HashMap<>();

            // Read from file
            File myObj = new File("fake_dataset.txt");
            Scanner myReader = new Scanner(myObj);
            String cateIdFromFile = null;
            List<DatasetCollectionEntity> datasetCollections = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                if (StringUtils.isBlank(data)) {
                    continue;
                }

                if (data.contains("----------------------------------")) {
                    if (StringUtils.isNotBlank(cateIdFromFile) &&
                            CollectionUtils.isNotEmpty(datasetCollections)) {
                        cateMapDataset.put(cateIdFromFile, new ArrayList<>(datasetCollections));
                    }

                    cateIdFromFile = null;
                    datasetCollections = new ArrayList<>();
                    continue;
                }

                if (data.contains("cateId")) {
                    String datas[] = data.split("_");
                    cateIdFromFile = datas[1];
                    continue;
                }

                if (data.contains("====")) {
                    data = myReader.nextLine();
                    String datasetName = data;

                    data = myReader.nextLine();
                    String datasetPicture = data;

                    data = myReader.nextLine();
                    String datasetDownloadPath = data;

                    datasetCollections.add(DatasetCollectionEntity.builder()
                            .name(datasetName)
                            .datasetCategoryId(cateIdFromFile)
                            .downloadPath(datasetDownloadPath)
                            .picture(datasetPicture)
                            .build());
                }
            }
            myReader.close();

            // Process to generate dataset
            Random rand = new Random();
            List<DatasetCollectionEntity> savedDatasets = new ArrayList<>();
            for (String cateId : cateMapDataset.keySet()) {
                List<DatasetCollectionEntity> datasets = cateMapDataset.get(cateId);
                for (DatasetCollectionEntity dataset : datasets) {
                    dataset.setShortDescription(shortDes);
                    dataset.setDescription(des);
                    dataset.setDatasetCategoryId(cateId);
                    dataset.setPreview(preview);
                    dataset.setAmount(rand.nextInt(60, 201));
                    savedDatasets.add(dataset);
                }
            }

            // add to db
            savedDatasets = datasetCollectionRepository.insert(savedDatasets);

            List<DatasetItemEntity> datasetItems = new ArrayList<>();
            for (DatasetCollectionEntity dataset : savedDatasets) {
                // Tao folder voi ten la id cua no
                if (new File("src/main/resources/dataset_collection/" + dataset.getId()).mkdirs()) {
                    // Copy file preview
                    FileChannel sourceChannel = null;
                    FileChannel destChannel = null;
                    try {
                        sourceChannel = new FileInputStream("src/main/resources/dataset_collection/64076f2a9ede6ac346c8da3f/preview.pdf").getChannel();
                        destChannel = new FileOutputStream("src/main/resources/dataset_collection/" + dataset.getId() + "/preview.pdf").getChannel();
                        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        sourceChannel.close();
                        destChannel.close();
                    }

                    // update preview path
                    dataset.setPreview("/dataset_collection/" + dataset.getId() + "/preview.pdf");

                    // create dataset item
                    datasetItems.add(DatasetItemEntity.builder()
                            .datasetCollectionId(dataset.getId())
                            .name("archive.zip")
                            .path(dataset.getDownloadPath())
                            .build());
                    dataset.setDownloadPath(null);
                }
            }

            // update to db
            datasetCollectionRepository.saveAll(savedDatasets);

            // save all dataset_items
            datasetItemRepository.saveAll(datasetItems);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/fetch_fake_dataset_to_file")
    public boolean fetchFakeDatasetToFile() {
        try {
            // Tạo file + ghi cateId
            PrintStream fileStream = new PrintStream(new File("fake_dataset_api.txt"));
            String cateId = "64455fd48be6f94675e9f092";
            String cateName = "Travel";
            fileStream.println("cateId_" + cateId + "_" + cateName);

            // Send Request and get the Response
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, "{\"page\":1,\"group\":\"PUBLIC\",\"size\":\"ALL\",\"fileType\":\"ALL\",\"license\":\"ALL\",\"viewed\":\"ALL\",\"categoryIds\":[16461],\"search\":\"\",\"sortBy\":\"HOTTEST\",\"hasTasks\":false,\"includeTopicalDatasets\":false}");
            Request request = new Request.Builder()
                    .url("https://www.kaggle.com/api/i/datasets.DatasetService/SearchDatasets")
                    .method("POST", body)
                    .addHeader("authority", "www.kaggle.com")
                    .addHeader("accept", "application/json")
                    .addHeader("accept-language", "en-US,en;q=0.9")
                    .addHeader("content-type", "application/json")
                    .addHeader("cookie", "ka_sessionid=75663ba133f04ee28b306b8dc0d63d41; ACCEPTED_COOKIES=true; _ga=GA1.1.1236238860.1677409287; __Host-KAGGLEID=CfDJ8PD62SAIer9In7uKL9OnnKn8e5Nst9RT4SdydTzW_d4R_udH_fMm11PKAwDyIiRYjacagkntedeijEzf8vGa-9JqBdgwi29-iuBJeO79MKwog-QmELviAP0J; _ga_KV7GH2Q8ZF=GS1.1.1683281561.2.0.1683281561.0.0.0; CSRF-TOKEN=CfDJ8PD62SAIer9In7uKL9OnnKn_HOG6naYAtZ_dQ6M-4UH91E2iFdFUbSRB6RoNzJIWwco8db5XGFOBOlxrThaboEZCzOdO1ZxktkPnqtB0vw; GCLB=CL30sq7hyO3JhgE; XSRF-TOKEN=CfDJ8PD62SAIer9In7uKL9OnnKlZsl3prqO46HY2Lw9uJR1mP7ZERECe94aqrSKiW-Yx7xjW5J4-RB6m8BgNFTptnMUAKwvPgw65_tmJQEgBrisQYTI5JRqdv4Snx8isAwgP2GQAr32j4PRZvAp9eqwGL1k; CLIENT-TOKEN=eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJrYWdnbGUiLCJhdWQiOiJjbGllbnQiLCJzdWIiOiJjYW9kaW5oc3l2eTEiLCJuYnQiOiIyMDIzLTA1LTA5VDA3OjQzOjA4LjczNjQ2OTFaIiwiaWF0IjoiMjAyMy0wNS0wOVQwNzo0MzowOC43MzY0NjkxWiIsImp0aSI6ImE0NGRjNGMwLTA1MzMtNGE1YS1hMDhmLTU1MzViODE3MTIwMSIsImV4cCI6IjIwMjMtMDYtMDlUMDc6NDM6MDguNzM2NDY5MVoiLCJ1aWQiOjE0NjIxMDYyLCJkaXNwbGF5TmFtZSI6IkNhbyBEaW5oIFN5IFZ5MSIsImVtYWlsIjoiY2FvdnkyMDAxQGdtYWlsLmNvbSIsInRpZXIiOiJOb3ZpY2UiLCJ2ZXJpZmllZCI6ZmFsc2UsInByb2ZpbGVVcmwiOiIvY2FvZGluaHN5dnkxIiwidGh1bWJuYWlsVXJsIjoiaHR0cHM6Ly9zdG9yYWdlLmdvb2dsZWFwaXMuY29tL2thZ2dsZS1hdmF0YXJzL3RodW1ibmFpbHMvZGVmYXVsdC10aHVtYi5wbmciLCJmZiI6WyJLZXJuZWxzRHJhZnRVcGxvYWRCbG9iIiwiS2VybmVsc0Rpc2FibGVVbnVzZWRBY2NlbGVyYXRvcldhcm5pbmciLCJLZXJuZWxzRmlyZWJhc2VMb25nUG9sbGluZyIsIkNvbW11bml0eUxvd2VySGVhZGVyU2l6ZXMiLCJBbGxvd0ZvcnVtQXR0YWNobWVudHMiLCJLZXJuZWxzU2F2ZUNlbGxPdXRwdXQiLCJUcHVPbmVWbSIsIlRwdVR3b1ZtRGVwcmVjYXRlZCIsIkZyb250ZW5kRXJyb3JSZXBvcnRpbmciLCJEYXRhc2V0c01hbmFnZWRGb2N1c09uT3BlbiIsIkRvaURhdGFzZXRUb21ic3RvbmVzIiwiQ2hhbmdlRGF0YXNldE93bmVyc2hpcFRvT3JnIiwiS2VybmVsRWRpdG9ySGFuZGxlTW91bnRPbmNlIiwiS2VybmVsUGlubmluZyIsIlBhZ2VWaXNpYmlsaXR5QW5hbHl0aWNzUmVwb3J0ZXIiLCJNYXVSZXBvcnQiLCJNb2RlbHNDYWNoZWRUYWdTZXJ2aWNlRW5hYmxlZCIsIkNvbXBldGl0aW9uc1J1bGVzS20iLCJEYXRhc2V0c1NoYXJlZFdpdGhUaGVtU2VhcmNoIl0sImZmZCI6eyJLZXJuZWxFZGl0b3JBdXRvc2F2ZVRocm90dGxlTXMiOiIzMDAwMCIsIkZyb250ZW5kRXJyb3JSZXBvcnRpbmdTYW1wbGVSYXRlIjoiMCIsIkVtZXJnZW5jeUFsZXJ0QmFubmVyIjoie30iLCJDbGllbnRScGNSYXRlTGltaXQiOiI0MCIsIkZlYXR1cmVkQ29tbXVuaXR5Q29tcGV0aXRpb25zIjoiMzUzMjUsMzcxNzQsMzM1NzksMzc4OTgsMzczNTQsMzc5NTksMzg4NjAiLCJBZGRGZWF0dXJlRmxhZ3NUb1BhZ2VMb2FkVGFnIjoiZGlzYWJsZWQifSwicGlkIjoia2FnZ2xlLTE2MTYwNyIsInN2YyI6IndlYi1mZSIsInNkYWsiOiJBSXphU3lBNGVOcVVkUlJza0pzQ1pXVnotcUw2NTVYYTVKRU1yZUUiLCJibGQiOiIwMTRhM2YzNDJkNmY4OGJjY2EyYjA5NTNlYjE5ZmM3YzkyZmE5ODAzIn0.; _ga_T7QHS60L4Q=GS1.1.1683620703.21.1.1683621005.0.0.0")
                    .addHeader("origin", "https://www.kaggle.com")
                    .addHeader("referer", "https://www.kaggle.com/datasets?tags=16113-Food")
                    .addHeader("sec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Google Chrome\";v=\"110\"")
                    .addHeader("sec-ch-ua-mobile", "?0")
                    .addHeader("sec-ch-ua-platform", "\"Linux\"")
                    .addHeader("sec-fetch-dest", "empty")
                    .addHeader("sec-fetch-mode", "cors")
                    .addHeader("sec-fetch-site", "same-origin")
                    .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
                    .addHeader("x-xsrf-token", "CfDJ8PD62SAIer9In7uKL9OnnKlZsl3prqO46HY2Lw9uJR1mP7ZERECe94aqrSKiW-Yx7xjW5J4-RB6m8BgNFTptnMUAKwvPgw65_tmJQEgBrisQYTI5JRqdv4Snx8isAwgP2GQAr32j4PRZvAp9eqwGL1k")
                    .build();
            Response response = client.newCall(request).execute();

            Map<String, Object> apiResult = objectMapper.readValue(response.body().string(), Map.class);
            Map<String, Object> datasetList = (Map<String, Object>) apiResult.get("datasetList");
            List<Object> items = (List<Object>) datasetList.get("items");

            for (Object itemObj: items) {
                fileStream.println("====");
                Map<String, Object> itemMap = (Map<String, Object>) itemObj;

                // Ghi name
                Map<String, Object> datasource = (Map<String, Object>) itemMap.get("datasource");
                String name = (String) datasource.get("title");
                fileStream.println(name);

                // Ghi link picture
                String pictureLink = (String) datasource.get("thumbnailImageUrl");
                if (pictureLink.contains("?")) {
                    pictureLink = pictureLink.substring(0, pictureLink.indexOf("?"));
                }
                pictureLink = pictureLink.replace("thumbnail", "cover");
                fileStream.println(pictureLink);

                // Ghi link download
                String downloadLink = (String) itemMap.get("downloadUrl");
                downloadLink = "https://www.kaggle.com" + downloadLink;
                fileStream.println(downloadLink);
            }

            fileStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
