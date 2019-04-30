package gov.ca.cwds.jobs.cals.facility.cws.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.XaCmsSessionFactory;
import gov.ca.cwds.cals.persistence.model.cwscms.SecondarySubstituteCareProviderInfo;
import gov.ca.cwds.cms.data.access.inject.DataAccessServicesSessionFactory;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.dao.CountiesDao;
import gov.ca.cwds.data.legacy.cms.entity.AddressPhoneticName;
import gov.ca.cwds.data.legacy.cms.entity.AddressPhoneticNamePK;
import gov.ca.cwds.data.legacy.cms.entity.ApplicationAndLicenseStatusHistory;
import gov.ca.cwds.data.legacy.cms.entity.BackgroundCheck;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.ClientOtherEthnicity;
import gov.ca.cwds.data.legacy.cms.entity.CountyLicenseCase;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnership;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnershipPK;
import gov.ca.cwds.data.legacy.cms.entity.EmergencyContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterface;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterfacePK;
import gov.ca.cwds.data.legacy.cms.entity.LicensingVisit;
import gov.ca.cwds.data.legacy.cms.entity.OtherAdultsInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherChildrenInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherEthnicity;
import gov.ca.cwds.data.legacy.cms.entity.OtherPeopleScpRelationship;
import gov.ca.cwds.data.legacy.cms.entity.OutOfHomePlacement;
import gov.ca.cwds.data.legacy.cms.entity.OutOfStateCheck;
import gov.ca.cwds.data.legacy.cms.entity.PhoneContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.PlacementEpisode;
import gov.ca.cwds.data.legacy.cms.entity.PlacementFacilityTypeHistory;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeInformation;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeInformationPK;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeNotes;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeProfile;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeProfilePK;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeUc;
import gov.ca.cwds.data.legacy.cms.entity.StaffPerson;
import gov.ca.cwds.data.legacy.cms.entity.SubCareProviderPhoneticName;
import gov.ca.cwds.data.legacy.cms.entity.SubstituteCareProvider;
import gov.ca.cwds.data.legacy.cms.entity.SubstituteCareProviderUc;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.County;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.FacilityType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.LicenseStatus;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.NameType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.State;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.VisitType;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.ReplicationPlacementHome;
import gov.ca.cwds.jobs.cals.facility.cws.dao.CwsChangedIdentifierDao;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier;
import gov.ca.cwds.jobs.common.inject.DataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class CwsCmsRsDataAccessModule extends DataAccessModule {

  public static final ImmutableList<Class<?>> cwsrsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          CwsChangedIdentifier.class
          , Client.class
          , OutOfHomePlacement.class
          , PlacementEpisode.class
          , ReplicationPlacementHome.class
          , PlacementHome.class
          , CountyLicenseCase.class
          , LicensingVisit.class
          , StaffPerson.class
          , FacilityType.class
          , County.class
          , VisitType.class
          , State.class
          , LicenseStatus.class
          , PlacementFacilityTypeHistory.class
          , AddressPhoneticName.class
          , AddressPhoneticNamePK.class
          , BackgroundCheck.class
          , OtherEthnicity.class
          , ClientOtherEthnicity.class
          , CountyOwnership.class
          , CountyOwnershipPK.class
          , EmergencyContactDetail.class
          , ExternalInterface.class
          , ExternalInterfacePK.class
          , PlacementHomeProfile.class
          , PlacementHomeProfilePK.class
          , PlacementHomeInformation.class
          , PlacementHomeInformationPK.class
          , PlacementHomeNotes.class
          , OtherPeopleScpRelationship.class
          , OutOfStateCheck.class
          , OtherAdultsInPlacementHome.class
          , OtherChildrenInPlacementHome.class
          , PhoneContactDetail.class
          , PlacementHomeUc.class
          , SubstituteCareProvider.class
          , SubstituteCareProviderUc.class
          , SubCareProviderPhoneticName.class
          , NameType.class
          , ApplicationAndLicenseStatusHistory.class
          , SecondarySubstituteCareProviderInfo.class
      ).build();

  public CwsCmsRsDataAccessModule(DataSourceFactory dataSourceFactory) {
    super(dataSourceFactory, DataSourceName.CWSRS.name(), cwsrsEntityClasses);
  }

  @Override
  protected void configure() {
    bind(CwsChangedIdentifierDao.class);
    bind(CountiesDao.class);
    bind(ClientDao.class);
  }

  @Provides
  @CmsSessionFactory
  public SessionFactory cmsSessionFactory() {
    return getSessionFactory();
  }

  @Provides
  @XaCmsSessionFactory
  public SessionFactory xaCmsSessionFactory() {
    return getSessionFactory();
  }

  @Provides
  @DataAccessServicesSessionFactory
  public SessionFactory dataAccessSessionFactory() {
    return getSessionFactory();
  }

}
