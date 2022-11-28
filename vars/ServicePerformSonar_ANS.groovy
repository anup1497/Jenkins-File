def call(String serviceName) {
 
  def services = []
  
  services.add('common-itr-utils-tn')
  services.add('service-users-tn')
  services.add('tn-utils')
  services.add('tn-config')
  services.add('service-otp-tn')
 
 
  services.add('service-notification-ans')
  services.add('service-otp-ans')
  //services.add('service-dms-ans')
  services.add('service-oneform-ans')
  services.add('service-aadhar-ans')
  services.add('service-loans-retail-ans')
  services.add('service-proposal-view-retail-ans')
  services.add('service-proposal-view-msme-ans')
  services.add('service-proposal-view-agri-ans')
  services.add('service-proposalview-livelihood-ans')
  services.add('service-users-ans')
  services.add('service-loans-ans')
  services.add('service-scheme-ans')
  
  
              
 
  return services.contains(serviceName)
    
}
