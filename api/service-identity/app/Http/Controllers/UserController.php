<?php

namespace App\Http\Controllers;

use App\Application\DTOs\User\CreateUserDTO;
use App\Application\UseCases\User\CreateUserUseCase;
use App\Application\Exceptions\EmailAlreadyExistsException;
use App\Http\Requests\StoreUserRequest;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use App\Http\Requests\UpdateProfileRequest;
use App\Application\DTOs\User\UpdateUserDTO;
use App\Application\UseCases\User\UpdateUserUseCase;
use App\Application\Exceptions\InvalidCurrentPasswordException;
use App\Application\UseCases\User\DeleteUserUseCase;

class UserController extends Controller
{
    public function store(StoreUserRequest $request, CreateUserUseCase $useCase): JsonResponse
    {
        $dto = new CreateUserDTO(
            name: $request->input('name'),
            email: $request->input('email'),
            password: $request->input('password'),
            birthDate: $request->input('birth_date')
        );

        try {
            $token = $useCase->execute($dto);

            return response()->json(['token' => $token], 201);

        } catch (EmailAlreadyExistsException $e) {
            return response()->json(['error' => __($e->getMessage())], 409);
        } catch (\Exception $e) {
            return response()->json(['error' => 'Internal Server Error'], 500);
        }
    }

    public function updateProfile(UpdateProfileRequest $request, UpdateUserUseCase $useCase)
    {
        $validated = $request->validated();

        $email = auth('api')->user()->email;

        $dto = new UpdateUserDTO(
            email: $email,
            name: $validated['name'] ?? null,
            currentPassword: $validated['current_password'] ?? null,
            newPassword: $validated['new_password'] ?? null
        );

        try {
            $useCase->execute($dto);
            return response()->json(['message' => 'Profile updated successfully'], 200);

        } catch (InvalidCurrentPasswordException $e) {
            return response()->json(['error' => __($e->getMessage())], 403);

        } catch (\Exception $e) {
            return response()->json(['error' => $e->getMessage()], 400);
        }
    }

    public function deleteProfile(DeleteUserUseCase $useCase)
    {
        try {
            $email = auth('api')->user()->email;

            $useCase->execute($email);

            return response()->json(['message' => 'User deleted successfully'], 200);

        } catch (\Exception $e) {
            return response()->json(['error' => $e->getMessage()], 400);
        }
    }
}
