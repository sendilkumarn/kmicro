import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './invoice.reducer';
import { IInvoice } from 'app/shared/model/kinvoice/invoice.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IInvoiceUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const InvoiceUpdate = (props: IInvoiceUpdateProps) => {
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { invoiceEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/invoice' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.date = convertDateTimeToServer(values.date);
    values.paymentDate = convertDateTimeToServer(values.paymentDate);

    if (errors.length === 0) {
      const entity = {
        ...invoiceEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kstoreApp.kinvoiceInvoice.home.createOrEditLabel">
            <Translate contentKey="kstoreApp.kinvoiceInvoice.home.createOrEditLabel">Create or edit a Invoice</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : invoiceEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="invoice-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="invoice-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="codeLabel" for="invoice-code">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.code">Code</Translate>
                </Label>
                <AvField
                  id="invoice-code"
                  type="text"
                  name="code"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="dateLabel" for="invoice-date">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.date">Date</Translate>
                </Label>
                <AvInput
                  id="invoice-date"
                  type="datetime-local"
                  className="form-control"
                  name="date"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.invoiceEntity.date)}
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="detailsLabel" for="invoice-details">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.details">Details</Translate>
                </Label>
                <AvField id="invoice-details" type="text" name="details" />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="invoice-status">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.status">Status</Translate>
                </Label>
                <AvInput
                  id="invoice-status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && invoiceEntity.status) || 'PAID'}
                >
                  <option value="PAID">{translate('kstoreApp.InvoiceStatus.PAID')}</option>
                  <option value="ISSUED">{translate('kstoreApp.InvoiceStatus.ISSUED')}</option>
                  <option value="CANCELLED">{translate('kstoreApp.InvoiceStatus.CANCELLED')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="paymentMethodLabel" for="invoice-paymentMethod">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.paymentMethod">Payment Method</Translate>
                </Label>
                <AvInput
                  id="invoice-paymentMethod"
                  type="select"
                  className="form-control"
                  name="paymentMethod"
                  value={(!isNew && invoiceEntity.paymentMethod) || 'CREDIT_CARD'}
                >
                  <option value="CREDIT_CARD">{translate('kstoreApp.PaymentMethod.CREDIT_CARD')}</option>
                  <option value="CASH_ON_DELIVERY">{translate('kstoreApp.PaymentMethod.CASH_ON_DELIVERY')}</option>
                  <option value="PAYPAL">{translate('kstoreApp.PaymentMethod.PAYPAL')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="paymentDateLabel" for="invoice-paymentDate">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.paymentDate">Payment Date</Translate>
                </Label>
                <AvInput
                  id="invoice-paymentDate"
                  type="datetime-local"
                  className="form-control"
                  name="paymentDate"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.invoiceEntity.paymentDate)}
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="paymentAmountLabel" for="invoice-paymentAmount">
                  <Translate contentKey="kstoreApp.kinvoiceInvoice.paymentAmount">Payment Amount</Translate>
                </Label>
                <AvField
                  id="invoice-paymentAmount"
                  type="text"
                  name="paymentAmount"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                    number: { value: true, errorMessage: translate('entity.validation.number') },
                  }}
                />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/invoice" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  invoiceEntity: storeState.invoice.entity,
  loading: storeState.invoice.loading,
  updating: storeState.invoice.updating,
  updateSuccess: storeState.invoice.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(InvoiceUpdate);
