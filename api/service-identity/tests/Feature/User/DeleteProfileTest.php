<?php

namespace Tests\Feature\User;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;
use App\Infrastructure\Persistence\Eloquent\UserModel;
use PHPOpenSourceSaver\JWTAuth\Facades\JWTAuth;
use Illuminate\Support\Facades\Event;
use App\Domain\User\Events\UserDeletedEvent;

class DeleteProfileTest extends TestCase
{
    use RefreshDatabase;

    public function test_it_returns_401_if_unauthenticated(): void
    {
        $response = $this->deleteJson('/api/me');
        $response->assertStatus(401);
    }

    public function test_it_deletes_user_and_fires_event(): void
    {
        Event::fake();

        $user = UserModel::create([
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => password_hash('StrongPassword123!', PASSWORD_BCRYPT),
            'birth_date' => '1990-01-01',
        ]);

        $token = JWTAuth::fromUser($user);

        $response = $this->withHeaders(['Authorization' => 'Bearer ' . $token])
            ->deleteJson('/api/me');

        $response->assertStatus(200)
            ->assertJson(['message' => 'User deleted successfully']);

        $this->assertDatabaseMissing('users', [
            'email' => 'jonas@example.com'
        ]);

        Event::assertDispatched(UserDeletedEvent::class, function ($event) {
            return $event->email === 'jonas@example.com';
        });
    }
}
