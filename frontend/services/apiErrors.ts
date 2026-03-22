import axios from 'axios';
import { toast } from 'sonner';


const BACKEND_ERROR_MAP: Record<string, string> = {
  'identity.auth.errors.invalid_credentials':  'errors.invalid_credentials',
  'identity.auth.errors.invalid_refresh_token': 'errors.invalid_refresh_token',

  'identity.user.errors.email_already_exists':    'errors.email_already_exists',
  'identity.user.errors.invalid_name':            'errors.invalid_name',
  'identity.user.errors.same_password':           'errors.same_password',
  'identity.user.errors.invalid_current_password':'errors.invalid_current_password',
  'identity.user.errors.invalid_password':        'errors.invalid_password',
  'identity.user.errors.invalid_email':           'errors.invalid_email',
  'identity.user.errors.invalid_birth_date':      'errors.invalid_birth_date',
};


export function resolveApiErrorKey(backendError?: string): string {
  if (!backendError) return 'errors.generic';
  return BACKEND_ERROR_MAP[backendError] ?? 'errors.generic';
}

/**
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
    if (onInline) {
      onInline(responseData.errors);
    }
    toast.warning(t('toast.validation_error'), { description: t('toast.validation_error_desc') });
    return;
  }

  if (status === 429) {
    toast.error(t('toast.too_many_attempts'), { description: t('toast.too_many_attempts_desc') });
    return;
  }
  if (responseData?.error) {
    const i18nKey = resolveApiErrorKey(responseData.error);
    const message = i18nKey !== 'errors.generic'
      ? t(i18nKey)
      : (responseData.error || t('errors.generic'));

    const toastTitle = status === 401
      ? t('toast.invalid_credentials')
      : t('toast.server_error');

    toast.error(toastTitle, { description: message });
    return;
  }

  toast.error(t('toast.server_error'), { description: t('toast.server_error_desc') });
}
