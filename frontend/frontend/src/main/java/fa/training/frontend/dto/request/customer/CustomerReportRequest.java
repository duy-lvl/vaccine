package fa.training.frontend.dto.request.customer;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CustomerReportRequest {
    private String dobFrom;
    private String dobTo;
    private String fullName;
    private String address;
    private int page;
    private int size;

    public int getPage() {
        return page > 1 ? page : 1;
    }
    public int getSize() {
        return size > 0 ? size : 5;
    }

    public String getDobFrom() {
        return dobFrom;
    }

    public String getDobTo() {
        return dobTo;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }
}