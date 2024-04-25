import { DeliveryCompany } from "./DeliveryCompany";

export interface GlobalConf {
  id? : string;
  applicationName?: string;
  deliveryCompany?: DeliveryCompany;
  comment?: string;
  exchangeComment?: string;
  cronExpression?: string;
}
