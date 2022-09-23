package com.mouken.party;

import com.mouken.domain.Account;
import com.mouken.domain.Party;
import com.mouken.domain.Tag;
import com.mouken.domain.Zone;
import com.mouken.party.form.PartyDescriptionForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final ModelMapper modelMapper;

    public Party createNewParty(Party party, Account account) {
        log.info("createNewParty");

        Party newParty = partyRepository.save(party);
        log.info("newParty Path = {}", newParty.getPath());
        newParty.addManager(account);
        return newParty;
    }

    public Party getParty(String path) {
        Party party = this.partyRepository.findByPath(path);
        checkIfExistingParty(path, party);

        return party;
    }

    public Party getPartyToUpdate(Account account, String path) {
        Party party = this.getParty(path);
        checkIfManager(account, party);
        return party;
    }

    public void updatePartyDescription(Party party, PartyDescriptionForm partyDescriptionForm) {
        modelMapper.map(partyDescriptionForm, party);
    }

    public void updatePartyBanner(Party party, String image) {
        party.setImage(image);
    }
    
    public void enablePartyBanner(Party party) {
        party.setUseBanner(true);
    }

    public void disablePartyBanner(Party party) {
        party.setUseBanner(false);
    }

    public void addTag(Party party, Tag tag) {
        party.getTags().add(tag);
    }

    public void removeTag(Party party, Tag tag) {
        party.getTags().remove(tag);
    }

    public void addZone(Party party, Zone zone) {
        party.getZones().add(zone);
    }

    public void removeZone(Party party, Zone zone) {
        party.getZones().remove(zone);
    }

    public Party getPartyToUpdateTag(Account account, String path) {
        Party party = partyRepository.findPartyWithTagsByPath(path);
        checkIfExistingParty(path, party);
        checkIfManager(account, party);
        return party;
    }

    public Party getPartyToUpdateZone(Account account, String path) {
        Party party = partyRepository.findPartyWithZonesByPath(path);
        checkIfExistingParty(path, party);
        checkIfManager(account, party);
        return party;
    }

    public Party getPartyToUpdateStatus(Account account, String path) {
        Party party = partyRepository.findPartyWithManagersByPath(path);
        checkIfExistingParty(path, party);
        checkIfManager(account, party);
        return party;
    }
    
    private void checkIfManager(Account account, Party party) {
        if (!account.isManagerOf(party)) {
            throw new AccessDeniedException("You're inaccessible.");
        }
    }

    private void checkIfExistingParty(String path, Party party) {
        if (party == null) {
            throw new IllegalArgumentException(path + " is invalid.");
        }
    }

    public void publish(Party party) {
        party.publish();
    }

    public void close(Party party) {
        party.close();
    }

    public void startRecruit(Party party) {
        party.startRecruit();
    }

    public void stopRecruit(Party party) {
        party.stopRecruit();
    }

    public String getNewPath() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    public boolean isValidPath(String newPath) {
        return !partyRepository.existsByPath(newPath);
    }

    public void updatePartyPath(Party party, String newPath) {
        party.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updatePartyTitle(Party party, String newTitle) {
        party.setTitle(newTitle);
    }

    public void remove(Party party) {
        if (party.isRemovable()) {
            partyRepository.delete(party);
        } else {
            throw new IllegalArgumentException("You can not delete this party.");
        }
    }

    public void addMember(Party party, Account account) {
        party.addMember(account);
    }

    public void removeMember(Party party, Account account) {
        party.removeMember(account);
    }

    public Party getPartyToEnroll(String path) {
        Party party = partyRepository.findPartyOnlyByPath(path);
        checkIfExistingParty(path, party);
        return party;
    }
}
