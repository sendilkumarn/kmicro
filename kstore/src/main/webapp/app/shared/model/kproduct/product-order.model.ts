import { Moment } from 'moment';
import { IOrderItem } from 'app/shared/model/kproduct/order-item.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IProductOrder {
  id?: number;
  placedDate?: string;
  status?: OrderStatus;
  code?: string;
  invoiceId?: number;
  customer?: string;
  orderItems?: IOrderItem[];
}

export const defaultValue: Readonly<IProductOrder> = {};
