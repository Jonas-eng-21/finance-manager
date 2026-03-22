import axios from 'axios';
import { toast } from 'sonner';


const IDENTITY_ERROR_MAP: Record<string, string> = {
  'identity.auth.errors.invalid_credentials':   'errors.invalid_credentials',
  'identity.auth.errors.invalid_refresh_token': 'errors.invalid_refresh_token',
  'identity.user.errors.email_already_exists':     'errors.email_already_exists',
  'identity.user.errors.invalid_name':             'errors.invalid_name',
  'identity.user.errors.same_password':            'errors.same_password',
  'identity.user.errors.invalid_current_password': 'errors.invalid_current_password',
  'identity.user.errors.invalid_password':         'errors.invalid_password',
  'identity.user.errors.invalid_email':            'errors.invalid_email',
  'identity.user.errors.invalid_birth_date':       'errors.invalid_birth_date',
};

const FINANCIAL_ERROR_MAP: Record<string, string> = {
  'category.validation.name.already_exists': 'errors.financial.category_already_exists',
  'category.validation.not_found':           'errors.financial.category_not_found',
  'category.validation.access_denied':       'errors.financial.access_denied',

  'transaction.validation.not_found':           'errors.financial.transaction_not_found',
  'transaction.validation.goal.invalid_type':   'errors.financial.goal_invalid_type',
  'transaction.validation.category_deleted':    'errors.financial.category_deleted',
  'transaction.validation.invalid_date_range':  'errors.financial.invalid_date_range',
  'transaction.validation.invalid_amount_range':'errors.financial.invalid_amount_range',
  'transaction.validation.month.invalid':       'errors.financial.month_invalid',

  'goal.validation.name.duplicated':            'errors.financial.goal_name_duplicated',
  'goal.not_found':                             'errors.financial.goal_not_found',
  'goal.already_deleted':                       'errors.financial.goal_already_deleted',
  'goal.already_archived':                      'errors.financial.goal_already_archived',

  'database.error.conflict':                    'errors.financial.conflict',
};

export function resolveApiErrorKey(backendError?: string): string {
  if (!backendError) return 'errors.generic';
  return (
    IDENTITY_ERROR_MAP[backendError] ??
    FINANCIAL_ERROR_MAP[backendError] ??
    'errors.generic'
  );
}

/**
 *
 * @param err      
 * @param t         
 * @param onInline
 */
export function handleApiError(
  err: unknown,
  t: (key: string) => string,
  onInline?: (errors: Record<string, string[]>) => void
): void {
  if (!axios.isAxiosError(err)) {
    toast.error(t('toast.network_error'), { description: t('toast.network_error_desc') });
    return;
  }

  const status = err.response?.status;
  const responseData = err.response?.data;

  if (status === 422 && responseData?.errors) {
    if (onInline) onInline(responseData.errors);
    toast.warning(t('toast.validation_error'), { description: t('toast.validation_error_desc') });
    return;
  }

  if (status === 400 && responseData?.message) {
    toast.error(t('toast.validation_error'), { description: responseData.message });
    return;
  }

  if (status === 404) {
    const desc = responseData?.message || t('errors.generic');
    toast.error(t('toast.not_found'), { description: desc });
    return;
  }

  if (status === 409) {
    const desc = responseData?.message || t('errors.financial.conflict');
    toast.error(t('toast.conflict'), { description: desc });
    return;
  }

  if (status === 429) {
    toast.error(t('toast.too_many_attempts'), { description: t('toast.too_many_attempts_desc') });
    return;
  }
  if (status === 401) {
    const i18nKey = resolveApiErrorKey(responseData?.error);
    const desc = i18nKey !== 'errors.generic' ? t(i18nKey) : (responseData?.error || t('errors.generic'));
    toast.error(t('toast.invalid_credentials'), { description: desc });
    return;
  }

  if (status === 403) {
    toast.error(t('toast.forbidden'), { description: responseData?.message || t('errors.financial.access_denied') });
    return;
  }
  toast.error(t('toast.server_error'), { description: t('toast.server_error_desc') });
}
