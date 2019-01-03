# HomeAway Coding Challenge

An application integrating with FourSquareAPI to allow users to search for venues, see the list of venues on a map and
view venue details for a given venue.

## Application Architecture

There are five modules that are included as part of the application architecture. I will break each of
these down below.

### Model

The Model Layer only contains the raw model that reflects the responses that come from the Network layer. This layer will only be data
classes that are shared with parent project layers. Given the overall simplicity of this layer, there is no need to implement unit
tests as there is no business logic.

### Network

The Network layer will contain all network interaction integration with the FourSquare Venue Search and Venue Lookup APIs. I utilized
retrofit as an HTTP Client when interfacing with external networks. The interface utilized to generate the Retrofit API is called
FourSquareAPI. I elected to utilize RXJava in conjunction with Retrofit so I can easily manage these tasks in other modules.
Since there is some additional parsing that can be done on the objects returned from the FourSquare service before passing elements to
calling classes, I have also included a FourSquareManager class that will strip these unnecessary wrapper classes from network
responses. This class could also be utilized to perform any additional business logic needed before returning a Single to the UI. In the
network module, there is also dependency injection required to build the FourSquareManager as well as provide it later on. In order to
have dagger correctly build the application graph, we must include NetworkModule as a module in the final Component class.

#### Network Testing

The Networking layer has testing of both the FourSquareAPI and FourSquareManager. When testing the FourSquare API, we utilize a MockWebServer
on a custom instance of our FourSquareAPI so all traffic will work through this server. To queue responses, we use a TestUtil class that
opens files that contain Json that directly correlates to the Json returned from FourSquare. We are able to validate the content returned from
the FourSquare service here as well as make sure our integration stays consistent by testing the Query Parameters, base URL and request types.

#### Testing Dependency from Other Classes

The Network layer also provides a custom implementation of the FourSquareAPI that will be pointed to "localhost://8080". This will be utilized
by our Espresso testing suite that I will go into further detail on when talking through the app layer. There is an additional create() method
on the FourSquareAPI that allows us to create the Four Square API with a custom URL. To ensure that dagger creates a graph that contains the
FourSquareAPI pointed to local host, an implementing component must include the MockNetworkModule.


### ViewModel

The ViewModel layer will contain all business logic needed to display view components in the application. We will utilize LiveData from Google's architecture
components library to notify views of updated components from any asynchronous actions, whether it be a network call or a database action. Each ViewModel will
extend the BaseViewModel. The BaseViewModel will manage disposable subscriptions to prevent memory leaks. In the ViewModel layer, we also map a Venue into a
DisplayVenue so that we only share elements needed by the UI and don't have to add any addtional business logic to our Fragment/Activity classes. The
View Model layer also provides a module to be included into the base Dagger component. We bind all View Models using a custom key, @ViewModelKey so that we
can utilize the map created by Dagger to inject the ViewModelFactory.

#### VenueDetailViewModel

The VenueDetailViewModel will contain any interaction needed to support the VenueDetail Screen. This View Model will be interact with the FourSquareAPI
when loading venue details. Upon loading VenueDetails, it will update the live data object containing a venue with a new venue that has a Venue's URL

#### VenueListViewModel

The VenueListViewModel will handle loading a list of venues from a given search term, updating whether or not an item is favorited in the VenueDatabase
and building a GoogleMapsUrl for a given venue. Upon successfully receiving a list of venues from the FourSquareApi, we will query the venue database to
see if any of the venues provided from four square have been previously favorited by the user. Upon receiving this list, we will merge the results and
create a list of DisplayVenues that will be used to populate a list of venues sent to the app layer. Upon successfully merging the results of our database
query and foursquare api lookup, we synchronously load a corresponding GoogleMaps static image to cache that will be displayed on the VenueDetails page.
When a user taps on the favorite icon, we call the venue database and either insert or remove the item into the VenueDatabase depending on whether or not
the venue was previously favorited.

#### VenueSharedViewModel

This ViewModel will be utilized to share values between fragments; it is intended to be used at the activity level.

#### ViewModel Testing

The VenueDetail and VenueList ViewModels have corresponding unit tests that test the loading a Venue List, loading a given venue's details and building the
Static Maps Image. Each test will extend a Base Test so we can override RXSchedulers to use a custom TestScheduler to ensure Actions from RX are scheduled
appropriately. When testing the view models, we mock the database and foursquareapi implementations and return built responses so we are only testing the
viewmodel components in these tests.

### Database

The database layer will contain all interactions with the database that is built for persisting favorited venue's across sessions. I chose to utilize
Room for interfacing with local databases. The VenueDao class is the main interface utilized when interacting with Room. This class is utilized to build
our VenueDatabase that will only be a list of Venue Ids. It will handle inserting, removing and querying the database. Similar to the network layer, there
is a class, VenueDatabaseManager, that will handle any additional business logic needed between the database layer and the implementing layer. The database
layer also requires injection and includes a DatabaseModule that will provide the VenueDatabaseManager and the VenueDao.

#### Database Testing

The Database layer is tested with VenueDatabaseManagerTest and VenueDatabaseTest. The VenueDatabaseTest will test the VenueDao implementation utilizing the
Room testing library so we can execute Database queries on the main thread. It will execute remove, insert and query and then validate the contents of the
database after testing these actions. In the VenueDatabaseManagerTest, we will mock the VenueDao object and stub responses from the DAO.

### App