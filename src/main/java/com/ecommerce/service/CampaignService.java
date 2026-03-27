package com.ecommerce.service;

import com.ecommerce.model.Campaign;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
    private final List<Campaign> campaigns = new ArrayList<>();

    @PostConstruct
    void initCampaigns() {
        campaigns.add(new Campaign("Weekend Campaign", "Up to 40% Off",
                "Selected keyboards, audio gear, and daily-use accessories with quick checkout offers.", true));
        campaigns.add(new Campaign("Free Delivery", "Orders over $99",
                "Extra shipping perks for the most popular products in the catalog.", false));
        campaigns.add(new Campaign("Flash Picks", "Tonight Only",
                "Best sellers and 5-star items rotate faster during the campaign window.", false));
    }

    public List<Campaign> getCampaigns() {
        return Collections.unmodifiableList(campaigns);
    }

    public void addCampaign(Campaign campaign) {
        campaigns.add(0, campaign);
        while (campaigns.size() > 3) {
            campaigns.remove(campaigns.size() - 1);
        }
    }
}
