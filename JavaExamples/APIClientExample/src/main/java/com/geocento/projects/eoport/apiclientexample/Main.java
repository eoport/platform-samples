package com.geocento.projects.eoport.apiclientexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.projects.eoport.api.*;
import com.geocento.projects.eoport.model.*;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Main {

    static private String username = System.getProperty("eoportusername");
    static private String password = System.getProperty("eoportpassword");

    static private ObjectMapper objectMapper = new ObjectMapper() {};

    static SimpleDateFormat fmtday = new SimpleDateFormat("yyMMdd");
    static {
        fmtday.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) throws Exception {

        EOPortApiUtil apiUtil = new EOPortApiUtil(username, password);

//        logUserProfile(apiUtil);

        logServiceProviders(apiUtil);

        logServices(apiUtil);

        logSubscriptions(apiUtil);

        logInvoices(apiUtil);
    }

    public static void logUserProfile(EOPortApiUtil apiUtil) throws Exception {
        AccountResourceApi accountClientApi = apiUtil.getAccountApi();
        UserProfile userProfile = accountClientApi.getUserProfile();
        System.out.println("User profile: " + objectMapper.writeValueAsString(userProfile));
    }

    public static void logServiceProviders(EOPortApiUtil apiUtil) throws Exception {
        ServiceProvidersResourceApi serviceProvidersResourceApi = apiUtil.getServiceProvidersResourceApi();
        ServiceProviders serviceProviders = serviceProvidersResourceApi.listServiceProviders(
                null,
                100,
                null,
                0,
                null,
                0);

        System.out.println("Found " + serviceProviders.getTotal() + " service providers");

        // display first service provider from the list
        if(serviceProviders.getTotal() == 0) {
            return;
        }
        ServiceProviderFull serviceProvider = serviceProvidersResourceApi.getServiceProvider(serviceProviders.getServiceProviders().get(0).getId());
        System.out.println("Service provider: " + objectMapper.writeValueAsString(serviceProvider));
    }

    public static void logServices(EOPortApiUtil apiUtil) throws Exception {
        ServicesResourceApi servicesResourceApi = apiUtil.getServicesResourceApi();
        ServiceOffers services = servicesResourceApi.listServiceOffers(
                null,
                null,
                null,
                0,
                null,
                0);

        System.out.println("Found " + services.getTotal() + " services");

        // display first service provider from the list
        if(services.getTotal() == 0) {
            return;
        }
        String serviceId = services.getServiceOffers().get(0).getId();
        ServiceOfferDescription service = servicesResourceApi.getServiceOfferDescription(serviceId);
        System.out.println("First service is '" + service.getName() + "': " + objectMapper.writeValueAsString(service));
    }

    private static void logSubscriptions(EOPortApiUtil apiUtil) throws Exception {
        SubscriptionsResourceApi subscriptionsResourceApi = apiUtil.getSubscriptionsResourceApi();
        Subscriptions subscriptions = subscriptionsResourceApi.getActiveSubscriptions(10, 0);


        System.out.println("Found " + subscriptions.getTotal() + " active subscriptions");

        // display first service provider from the list
        if(subscriptions.getTotal() == 0) {
            return;
        }
        String subscriptionId = subscriptions.getSubscriptions().get(0).getId();
        SubscriptionContent subscription = subscriptionsResourceApi.getSubscription(subscriptionId);
        System.out.println("First subscription is '" + subscription.getName() + "': " + objectMapper.writeValueAsString(subscription));

        if(subscription.getDeliveriesCount() == 0) {
            System.out.println("No products delivered to date");
        } else {
            System.out.println(subscription.getDeliveriesCount() + " products delivered to date");
            // get deliveries
            List<SubscriptionDeliveryDTO> deliveries = subscriptionsResourceApi.getSubscriptionDeliveries(subscriptionId, 10, 0);
            // find deliveries with a downloadable product
            List<SubscriptionDeliveryDTO> downloadableDeliveries = deliveries.stream().filter(subscriptionDeliveryDTO -> !StringUtils.isEmpty(subscriptionDeliveryDTO.getDownloadUrl())).collect(Collectors.toList());
            if(downloadableDeliveries.size() == 0) {
                System.out.println("No downloadable products");
            } else {
                // the download URL contains all credentials for direct download
                System.out.println("Found " + downloadableDeliveries.size() + " downloadable products, first product download URL is " +
                        downloadableDeliveries.get(0).getDownloadUrl());
            }
        }
    }

    private static void logInvoices(EOPortApiUtil apiUtil) throws Exception {
        InvoicesResourceApi invoicesResourceApi = apiUtil.getInvoicesResourceApi();
        // get invoices from the last 90 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        Date fromDate = calendar.getTime();
        Invoices invoices = invoicesResourceApi.getInvoices(toIntegerDate(fromDate), null);

        System.out.println("Found " + invoices.getTotal() + " invoices for the selected period");

        if(invoices.getTotal() > 0) {
            Invoice invoice = invoices.getInvoices().get(0);
            System.out.println("Download invoice #" + invoice.getId() + " at " +
                    invoice.getDownloadUrl() + ", " +
                    "you will need to add the bearer token to your request");
        }
    }

    static public Integer toIntegerDate(Date startDate) {
        return Integer.valueOf(fmtday.format(startDate));
    }
}
