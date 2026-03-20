package com.split.expenseSplitter.pojo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PostTransactionRequest {

    @NotEmpty(message = "You must provide at least one transaction.")
    List<@Valid RequestTransactions> transactions;

    public String toString() {
        return "PostTransactionRequest{" + "transactions=" + transactions + '}';
    }

    @NotEmpty(message = "You must provide at least one transaction.")
    public List<@NotNull(message="You must not have any blank transactions.") RequestTransactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<RequestTransactions> transactions) {
        this.transactions = transactions;
    }

    public static class RequestTransactions {
        @NotNull(message = "You must provide a spent amount.")
        Integer spentAmount;

        @NotBlank(message = "You must provide a spent by.")
        String spentBy;

        @NotBlank(message = "You must provide a spent on.")
        String spentOn;

        @NotBlank(message = "You must provide a spent date. DD/MM/YYYY")
        String spentDate;

        @NotEmpty(message = "You must provide at least one benefitted by.")
        List<@NotBlank(message = "Blank cannot be part of the benefittedby list.") String> benefittedBy;

        public String toString() {
            return "Transactions{" + "spentAmount=" + spentAmount + ", spentBy=" + spentBy + ", spentOn=" + spentOn + ", spentDate=" + spentDate + ", benefittedBy=" + benefittedBy + '}';
        }

        public Integer getSpentAmount() {
            return spentAmount;
        }

        public void setSpentAmount(Integer spentAmount) {
            this.spentAmount = spentAmount;
        }

        public String getSpentBy() {
            return spentBy;
        }

        public void setSpentBy(String spentBy) {
            this.spentBy = spentBy;
        }

        public String getSpentOn() {
            return spentOn;
        }

        public void setSpentOn(String spentOn) {
            this.spentOn = spentOn;
        }

        public String getSpentDate() {
            return spentDate;
        }

        public void setSpentDate(String spentDate) {
            this.spentDate = spentDate;
        }

        public List<String> getBenefittedBy() {
            return benefittedBy;
        }

        public void setBenefittedBy(List<String> benefittedBy) {
            this.benefittedBy = benefittedBy;
        }
    }
}
