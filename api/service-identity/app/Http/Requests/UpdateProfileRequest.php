<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class UpdateProfileRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    public function rules(): array
    {
        return [
            'name' => ['nullable', 'string', 'min:3', 'max:120'],
            'current_password' => ['nullable', 'string'],
            'new_password' => ['nullable', 'string', 'min:8'],
        ];
    }
}
