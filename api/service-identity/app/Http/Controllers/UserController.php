<?php

namespace App\Http\Controllers;

use App\Application\DTOs\User\CreateUserDTO;
use App\Application\UseCases\User\CreateUserUseCase;
use App\Application\Exceptions\EmailAlreadyExistsException;
use App\Http\Requests\StoreUserRequest;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;

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
}
